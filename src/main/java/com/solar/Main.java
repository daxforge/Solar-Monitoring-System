package com.solar;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Embedded Apache Tomcat 10.1 Launcher for Instant Local Execution.
 * Allows running the entire web application locally with a single command without separate Tomcat installation:
 * Usage: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProperty = System.getProperty("server.port", System.getenv("PORT"));
        if (portProperty != null) {
            try {
                port = Integer.parseInt(portProperty.trim());
            } catch (NumberFormatException ignored) {}
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Eagerly trigger default HTTP connector

        // Base directory for Tomcat work files
        File baseDirFile = new File("target/tomcat");
        baseDirFile.mkdirs();
        tomcat.setBaseDir(baseDirFile.getAbsolutePath());

        // Webapp root directory
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File("solar-monitoring-system/src/main/webapp");
        }

        Context context = tomcat.addWebapp("", webappDir.getAbsolutePath());
        context.setParentClassLoader(Main.class.getClassLoader());

        // Configure class loader so embedded Tomcat finds application classes
        File additionWebInfClasses = new File("target/classes");
        if (!additionWebInfClasses.exists()) {
            additionWebInfClasses = new File("solar-monitoring-system/target/classes");
        }

        if (additionWebInfClasses.exists()) {
            org.apache.catalina.WebResourceRoot resources = new org.apache.catalina.webresources.StandardRoot(context);
            resources.addPreResources(new org.apache.catalina.webresources.DirResourceSet(
                    resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"
            ));
            context.setResources(resources);
        }

        System.out.println("============================================================================");
        System.out.println("     HELIOS - Solar Energy Monitoring System (Jakarta EE 10 / Tomcat 10.1)");
        System.out.println("============================================================================");
        System.out.println(" Server initialized on port: " + port);
        System.out.println(" Portal URL:     http://localhost:" + port + "/");
        System.out.println(" Admin Login:    admin@solar.local   / admin123");
        System.out.println(" Homeowner:      alice@solar.local   / user123");
        System.out.println("============================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
