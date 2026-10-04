package com.solar.listener;

import com.solar.simulator.SolarDataSimulator;
import com.solar.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServletContextListener that initializes background worker threads upon application deployment
 * and ensures graceful thread termination upon server shutdown.
 */
@WebListener
public class AppStartupListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppStartupListener.class);

    private SolarDataSimulator simulator;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing Solar Energy Monitoring System...");

        // Eagerly initialize DB Connection to trigger auto-bootstrap if needed
        try {
            DBConnection.getInstance();
            logger.info("Database connectivity established.");
        } catch (Exception e) {
            logger.error("Database connection initialization warning: {}", e.getMessage());
        }

        // Start background simulator
        simulator = new SolarDataSimulator();
        simulator.start(5); // 5 second telemetry interval
        sce.getServletContext().setAttribute("solarSimulator", simulator);

        logger.info("Solar Energy Monitoring System initialized successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Destroying Solar Energy Monitoring System context...");

        if (simulator != null) {
            simulator.stop();
        }

        // Close HikariCP connection pool
        DBConnection.getInstance().closePool();

        logger.info("Context cleanly terminated.");
    }
}
