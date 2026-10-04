package com.solar.simulator;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.model.EnergyDevice;
import com.solar.service.DeviceService;
import com.solar.service.EnergyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background multi-threaded simulator for streaming realistic real-time telemetry.
 * Demonstrates Core Java Concurrency: ScheduledExecutorService, thread lifecycle, and graceful termination.
 */
public class SolarDataSimulator implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SolarDataSimulator.class);

    private final DeviceService deviceService;
    private final EnergyService energyService;
    private final Random random = new Random();

    private ScheduledExecutorService executorService;
    private volatile boolean running = false;

    // Track simulated battery levels
    private double simulatedBatteryAlice = 78.5;
    private double simulatedBatteryBob = 42.0;

    public SolarDataSimulator() {
        this.deviceService = new DeviceService();
        this.energyService = new EnergyService();
    }

    /**
     * Starts the recurring background simulation task every 5 seconds.
     */
    public synchronized void start(int intervalSeconds) {
        if (running) return;

        executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Solar-Simulator-Thread");
            t.setDaemon(true); // Allow JVM to exit cleanly
            return t;
        });

        int interval = intervalSeconds > 0 ? intervalSeconds : 5;
        executorService.scheduleAtFixedRate(this, 3, interval, TimeUnit.SECONDS);
        running = true;
        logger.info("Solar Data Simulator started with {}s execution interval.", interval);
    }

    /**
     * Gracefully stops the simulator executor.
     */
    public synchronized void stop() {
        if (!running || executorService == null) return;

        logger.info("Stopping Solar Data Simulator...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        running = false;
        logger.info("Solar Data Simulator stopped successfully.");
    }

    @Override
    public void run() {
        try {
            List<EnergyDevice> devices = deviceService.getAllDevices();
            if (devices.isEmpty()) return;

            LocalTime now = LocalTime.now();
            int hour = now.getHour();

            // Solar irradiance curve (0 outside 6am-7pm, bell curve peak at 12-1pm)
            double solarIntensityFactor;
            if (hour >= 6 && hour <= 19) {
                // Bell curve approx: sin((hour - 6) / 13 * PI)
                double angle = ((hour - 6) / 13.0) * Math.PI;
                solarIntensityFactor = Math.sin(angle);
            } else {
                solarIntensityFactor = 0.0;
            }

            for (EnergyDevice device : devices) {
                if (device.getStatus() != DeviceStatus.ONLINE) {
                    continue; // Skip offline or maintenance units
                }

                double capacity = device.getCapacityKw() != null ? device.getCapacityKw() : 5.0;
                double prodKw = 0.0;
                double consKw = 0.0;
                double battPct = 0.0;

                if (device.getType() == DeviceType.SOLAR_PANEL) {
                    // Slight variation +/- 10% for passing cloud effects
                    double cloudNoise = 0.85 + (random.nextDouble() * 0.3);
                    prodKw = capacity * solarIntensityFactor * cloudNoise;
                    prodKw = Math.max(0.0, Math.round(prodKw * 1000.0) / 1000.0);
                    consKw = Math.round((0.05 + random.nextDouble() * 0.15) * 1000.0) / 1000.0; // Inverter parasitic load
                    battPct = 0.0;
                } else if (device.getType() == DeviceType.BATTERY) {
                    // Update battery level
                    double delta = (solarIntensityFactor > 0.3) ? (0.3 + random.nextDouble() * 0.5) : -(0.2 + random.nextDouble() * 0.4);
                    if (device.getId() == 2) {
                        simulatedBatteryAlice = Math.min(100.0, Math.max(10.0, simulatedBatteryAlice + delta));
                        battPct = Math.round(simulatedBatteryAlice * 10.0) / 10.0;
                    } else {
                        simulatedBatteryBob = Math.min(100.0, Math.max(10.0, simulatedBatteryBob + delta));
                        battPct = Math.round(simulatedBatteryBob * 10.0) / 10.0;
                    }
                    prodKw = 0.0;
                    consKw = Math.round((0.3 + random.nextDouble() * 0.5) * 1000.0) / 1000.0;
                } else {
                    // Inverter or Smart Meter
                    prodKw = Math.round((capacity * solarIntensityFactor * 0.95) * 1000.0) / 1000.0;
                    consKw = Math.round((1.2 + random.nextDouble() * 1.5) * 1000.0) / 1000.0;
                    battPct = 75.0;
                }

                // Record reading to DB and evaluate threshold rules
                energyService.recordReading(device.getId(), prodKw, consKw, battPct);
            }
        } catch (Exception e) {
            logger.warn("Simulator cycle note: {}", e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }
}
