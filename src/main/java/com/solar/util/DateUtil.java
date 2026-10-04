package com.solar.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modern Date and Time utility wrapping the java.time API (JSR-310).
 */
public final class DateUtil {

    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:00");

    private DateUtil() {
        // Enforce non-instantiability
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_DATETIME_FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    public static String formatTimeHour(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(HOUR_FORMATTER);
    }

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(dateStr.trim(), INPUT_DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    /**
     * Resolves a named period ("today", "7days", "30days", or "custom") into start and end timestamps.
     * @return LocalDateTime array of [start, end]
     */
    public static LocalDateTime[] resolveDateRange(String period, String customFrom, String customTo) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now;
        LocalDateTime start;

        if ("today".equalsIgnoreCase(period)) {
            start = LocalDate.now().atStartOfDay();
        } else if ("30days".equalsIgnoreCase(period)) {
            start = now.minusDays(30);
        } else if ("custom".equalsIgnoreCase(period) && customFrom != null && !customFrom.trim().isEmpty()) {
            LocalDate fromDate = parseDate(customFrom);
            LocalDate toDate = (customTo != null && !customTo.trim().isEmpty()) ? parseDate(customTo) : LocalDate.now();
            start = fromDate.atStartOfDay();
            end = toDate.atTime(LocalTime.MAX);
        } else {
            // Default: 7 days
            start = now.minusDays(7);
        }

        return new LocalDateTime[]{start, end};
    }
}
