package me.cjcrafter.neat.util;

import java.util.concurrent.TimeUnit;

public class Timer {

    private long start;

    public void start() {
        this.start = System.nanoTime();
    }

    public double stop() {
        if (start == 0L)
            throw new IllegalStateException("Call to stop() before start()");

        long time = System.nanoTime() - start;
        start = 0L;

        int seconds = (int) TimeUnit.NANOSECONDS.toSeconds(time);
        double millis = (time % 1000000000L) / 1000000000.0;

        return seconds + millis;
    }
}
