package dev.phantom.client.util;

public class TimerUtil {

    private long lastTime = System.currentTimeMillis();

    // -------------------------------------------------------------------------
    // Instance methods
    // -------------------------------------------------------------------------

    /**
     * Returns true if the given number of milliseconds has passed since the last reset.
     */
    public boolean hasPassed(long milliseconds) {
        return System.currentTimeMillis() - lastTime >= milliseconds;
    }

    /**
     * Resets the timer to the current time.
     */
    public void reset() {
        lastTime = System.currentTimeMillis();
    }

    /**
     * Returns the number of milliseconds elapsed since the last reset.
     */
    public long getElapsed() {
        return System.currentTimeMillis() - lastTime;
    }

    // -------------------------------------------------------------------------
    // Static utility
    // -------------------------------------------------------------------------

    /**
     * Returns the current time in milliseconds.
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }
}
