package com.vn.es.util;

/**
 * Утилитный класс для работы с временем.
 *
 * @author Vladislav Nosov
 */
public final class TimeUtil {

    public static final long MILLI_PER_MINUTE = 60_000;
    public static final long MILLI_PER_HOUR = MILLI_PER_MINUTE * 60;
    public static final long MILLI_PER_DAY = MILLI_PER_HOUR * 24;

    private TimeUtil() {
        //NOP
    }

    public static long getTimeInMinutes(long timestamp) {
        return timestamp / MILLI_PER_MINUTE;
    }

    public static long getTimeInHours(long timestamp) {
        return timestamp / MILLI_PER_HOUR;
    }

    public static long getTimeInDays(long timestamp) {
        return timestamp / MILLI_PER_DAY;
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static long getCurrentTimeInMinutes() {
        return getTimeInMinutes(getCurrentTimestamp());
    }

    public static long getCurrentTimeInHours() {
        return getTimeInHours(getCurrentTimestamp());
    }

    public static long getCurrentTimeInDays() {
        return getTimeInDays(getCurrentTimestamp());
    }

    public static long minusDay(long timestamp) {
        return timestamp - MILLI_PER_DAY;
    }
}