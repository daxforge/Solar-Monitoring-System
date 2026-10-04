# ☀️ Helios — Intelligent Solar Energy Monitoring System

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-F05138?logo=eclipseide&logoColor=white)](https://jakarta.ee/)
[![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-10.1-F8DC75?logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An enterprise-grade, full-stack Clean Energy Monitoring and Management Web Application built with **Jakarta EE Servlets**, **JSP/JSTL**, **MySQL**, **HikariCP**, and modern glassmorphic responsive UI. 

**Helios** provides residential homeowners and system administrators with real-time visibility into photovoltaic (PV) generation, household consumption, battery storage telemetry, grid exchanges, and device health.

---

## ⚡ Key Features

### 🏡 Homeowner Portal
- **Real-Time Energy Flow**: Visual breakdown of live production, household consumption, battery charge/discharge rates, and net grid export/import.
- **Production Analytics**: Diurnal generation curves, daily/weekly yields, peak sun hours, and panel efficiency tracking.
- **Consumption Breakdown**: Analyze power usage patterns, appliance tier estimations, and financial savings calculated using custom tariffs.
- **Storage & Battery Telemetry**: Live State of Charge (SOC %), health status, cycle tracking, and autonomy estimation.
- **System Alerts & Notifications**: Real-time notifications for low production, battery thermal warnings, and inverter status changes.
- **Profile & Preferences**: Manage account settings, currency preferences, and notification thresholds.

### 🛡️ Administrator Operations
- **System Dashboard**: High-level telemetry aggregation across all managed solar installations, active users, and connected hardware.
- **Device Management**: Provision, inspect, and update solar panels, hybrid inverters, lithium batteries, and smart meters.
- **User Management**: Manage homeowner accounts, status toggling, and role assignments.
- **System Configuration**: Fine-tune global telemetry settings, electricity tariffs, low-production alarm limits, and poll frequencies.
- **Automated Data Simulator**: Built-in background engine producing realistic diurnal solar curves and fluctuating load profiles.

---

## 🏗️ Architecture & Technology Stack

| Layer | Technologies |
|---|---|
| **Runtime & Language** | Java 17 LTS |
| **Web Framework** | Jakarta Servlet API 6.0, Jakarta Server Pages (JSP) 3.1, JSTL 3.0 |
| **Application Server** | Embedded Apache Tomcat 10.1.24 |
| **Database & Pooling** | MySQL 8.0+, HikariCP 5.1 (High-performance connection pooling) |
| **Security** | BCrypt password hashing, session management, CSRF filters, Role-Based Access Control (RBAC) |
| **Data & Serialization** | Jackson 2.17 for JSON RESTful API endpoints |
| **Frontend & UI** | Vanilla Modern CSS (Glassmorphism, CSS Custom Properties, Dark Mode), Vanilla JavaScript, Chart.js 4.4 |
| **Build & Tooling** | Apache Maven 3.8+ |

---

## 📂 Project Structure

```
solar-monitoring-system/
├── database/
│   ├── schema.sql              # Complete DDL: tables, indexes, constraints, triggers
│   └── seed.sql                # Seed data: admin, homeowners, devices, readings, alerts
├── src/
│   └── main/
│       ├── java/com/solar/
│       │   ├── Main.java               # Embedded Tomcat bootstrap launcher
│       │   ├── dao/                    # DAO interfaces and JDBC HikariCP implementations
│       │   ├── enums/                  # Enums (Role, DeviceType, DeviceStatus, AlertSeverity)
│       │   ├── exception/              # Application custom exceptions
│       │   ├── filter/                 # Auth, Role, CSRF, and Encoding filters
│       │   ├── listener/               # AppStartupListener (lifecycle & background simulator)
│       │   ├── model/                  # Domain POJOs & OOP device hierarchy
│       │   ├── service/                # Business logic services
│       │   ├── servlet/                # MVC controllers & REST API endpoints
│       │   ├── simulator/              # Autonomous solar & energy generation simulator
│       │   └── util/                   # DBConnection, PasswordUtil, Validation, JsonUtil
│       ├── resources/
│       │   ├── db.properties           # Database URL, credentials, and HikariCP tuning
│       │   └── logging.properties      # JUL logging configuration
│       └── webapp/
│           ├── assets/
│           │   ├── css/                # base.css, components.css, layout.css, theme.css
│           │   └── js/                 # charts.js, live.js, ui.js
│           ├── WEB-INF/
│           │   ├── web.xml             # Deployment descriptor
│           │   └── views/              # JSP views (auth, homeowner, admin, common)
│           └── index.jsp               # Root landing & authentication redirector
├── pom.xml                             # Maven build configuration & dependencies
└── README.md                           # Documentation
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: Version 3.8 or higher
- **MySQL Server**: Version 8.0 or higher
- **Git**

### 2. Database Setup
1. Log in to MySQL CLI or MySQL Workbench:
   ```bash
   mysql -u root -p
   ```
2. Create and seed the database using the provided SQL scripts:
   ```sql
   source /path/to/solar-monitoring-system/database/schema.sql;
   source /path/to/solar-monitoring-system/database/seed.sql;
   ```

3. Configure your database credentials in `src/main/resources/db.properties`:
   ```properties
   db.driver=com.mysql.cj.jdbc.Driver
   db.url=jdbc:mysql://localhost:3306/solar_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.username=root
   db.password=YOUR_MYSQL_PASSWORD
   ```

### 3. Build & Run

#### Option A: Run via Embedded Tomcat (Recommended)
You can directly compile and launch the application using the embedded runner:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.solar.Main"
```
Once started, access the application in your browser:
👉 **http://localhost:8080**

#### Option B: Build WAR for External Tomcat
To package as a standard Web Archive (WAR):
```bash
mvn clean package
```
Deploy the generated `target/solar-monitoring-system.war` to your Tomcat 10.1+ `webapps/` folder.

---

## 🔑 Demo Credentials

| Role | Email | Password | Scope |
|---|---|---|---|
| **Administrator** | `admin@solar.local` | `admin123` | Full access to users, devices, settings & telemetry |
| **Homeowner** | `alice@solar.local` | `user123` | Villa South Wing (Solar Array + PowerVault Battery) |
| **Homeowner** | `bob@solar.local` | `user123` | Garden Terrace (Solar Pergola + EcoPack Battery) |
| **Homeowner** | `charlie@solar.local` | `user123` | Driveway Carport (Bifacial Panels + SunTracker) |

---

## 📊 Live API Endpoints

The system exposes RESTful JSON endpoints used by the dashboard for live chart updates:

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/energy/current` | Returns real-time production, consumption, battery SOC, and grid balance |
| `GET` | `/api/energy/chart?range=today` | Returns historical hourly energy datasets for Chart.js |
| `GET` | `/api/alerts/unread` | Retrieves current unread alerts and notification count |

---

## 📜 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
