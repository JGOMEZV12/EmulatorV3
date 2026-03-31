package com.polar.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UnixTimestamp {

    /**
     * Gets the current date time now in Unix Timestamp format.
     *
     * @return Unix Timestamp.
     */
    public static double getNow() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Converts the Unix Timestamp to a LocalDateTime object.
     *
     * @param timestamp Unix Timestamp.
     * @return LocalDateTime object.
     */
    public static LocalDateTime fromUnixTimestamp(double timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond((long) timestamp), ZoneId.systemDefault());
    }

    public static long getNowLong() {
        return Instant.now().getEpochSecond();
    }
}
