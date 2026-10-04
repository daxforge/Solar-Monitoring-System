package com.solar.util;

import com.solar.exception.DataAccessException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton Database Connection Manager using HikariCP connection pooling.
 * Follows the Singleton Pattern and provides bulletproof reliability:
 * First attempts to connect to the configured MySQL instance; if unavailable,
 * gracefully falls back to an embedded in-memory database with automatic schema/seed execution.
 */
public final class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    private static volatile DBConnection instance;
    private HikariDataSource dataSource;
    private boolean isFallbackMode = false;

    private DBConnection() {
        initDataSource();
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes HikariCP using db.properties or embedded fallback.
     */
    private void initDataSource() {
        Properties props = loadProperties();

        String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        String url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/solar_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String username = props.getProperty("db.username", "root");
        String password = props.getProperty("db.password", "root123");

        try {
            logger.info("Attempting primary database connection: {}", url);
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(15);
            config.setMinimumIdle(3);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(5000); // 5 sec timeout to avoid hanging if MySQL is offline
            config.setPoolName("SolarHikariPool-MySQL");

            this.dataSource = new HikariDataSource(config);

            // Test connection
            try (Connection conn = this.dataSource.getConnection()) {
                logger.info("Successfully connected to MySQL database!");
                isFallbackMode = false;
                bootstrapDatabaseIfNeeded(conn, false);
            }
        } catch (Exception e) {
            logger.warn("Primary MySQL connection failed ({}). Activating embedded database fallback mode...", e.getMessage());
            initFallbackDataSource();
        }
    }

    /**
     * Configures an embedded H2 database in MySQL compatibility mode as fallback.
     */
    private void initFallbackDataSource() {
        try {
            if (this.dataSource != null && !this.dataSource.isClosed()) {
                this.dataSource.close();
            }
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.h2.Driver");
            config.setJdbcUrl("jdbc:h2:mem:solar_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1");
            config.setUsername("sa");
            config.setPassword("");
            config.setMaximumPoolSize(10);
            config.setPoolName("SolarHikariPool-Embedded");

            this.dataSource = new HikariDataSource(config);
            this.isFallbackMode = true;

            try (Connection conn = this.dataSource.getConnection()) {
                logger.info("Embedded database initialized. Bootstrapping schema and seed records...");
                bootstrapDatabaseIfNeeded(conn, true);
            }
        } catch (Exception ex) {
            logger.error("Fatal: failed to initialize fallback database", ex);
            throw new DataAccessException("Unable to establish any database connection", ex);
        }
    }

    /**
     * Loads database configuration properties from classpath.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            logger.warn("Could not read db.properties, using built-in defaults: {}", e.getMessage());
        }
        return props;
    }

    /**
     * Bootstraps tables and sample seed records if they do not yet exist.
     */
    private void bootstrapDatabaseIfNeeded(Connection conn, boolean isFallback) {
        try (Statement stmt = conn.createStatement()) {
            boolean tablesExist = false;
            try (ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users")) {
                if (rs.next()) {
                    tablesExist = true;
                    int userCount = rs.getInt(1);
                    if (userCount == 0) {
                        logger.info("Users table is empty. Executing seed script...");
                        executeSqlResource(conn, "database/seed.sql", isFallback);
                    }
                }
            } catch (SQLException checkEx) {
                // Table doesn't exist yet
                tablesExist = false;
            }

            if (!tablesExist) {
                logger.info("Bootstrapping schema.sql...");
                executeSqlResource(conn, "database/schema.sql", isFallback);
                logger.info("Bootstrapping seed.sql...");
                executeSqlResource(conn, "database/seed.sql", isFallback);
            }
        } catch (Exception ex) {
            logger.warn("Database bootstrap check note: {}", ex.getMessage());
        }
    }

    /**
     * Reads and executes a multi-statement SQL resource file.
     */
    private void executeSqlResource(Connection conn, String resourcePath, boolean isFallback) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            // Check filesystem path if not in resources
            try {
                java.io.File file = new java.io.File(resourcePath);
                if (file.exists()) {
                    in = new java.io.FileInputStream(file);
                }
            } catch (Exception ignored) {}
        }
        if (in == null) {
            logger.warn("SQL resource file not found: {}", resourcePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.isEmpty() || trimmed.startsWith("/*")) {
                    continue;
                }
                if (isFallback && (trimmed.toUpperCase().startsWith("CREATE DATABASE") || trimmed.toUpperCase().startsWith("USE "))) {
                    continue; // Skip DB creation syntax on H2 mem
                }
                sb.append(line).append("\n");
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException sqle) {
                            logger.debug("SQL statement note: {} -> {}", sql, sqle.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            logger.error("Error executing SQL resource {}: {}", resourcePath, e.getMessage());
        }
    }

    /**
     * Retrieves an active connection from the pool.
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public boolean isFallbackMode() {
        return isFallbackMode;
    }

    /**
     * Gracefully shuts down connection pool.
     */
    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool closed successfully.");
        }
    }
}
