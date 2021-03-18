package me.cjcrafter.neat.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.TimeUnit;

public class Timer {

    private long start;
    private double time;

    public void start() {
        this.start = System.nanoTime();
    }

    public void stop() {
        if (start == 0L)
            throw new IllegalStateException("Call to stop() before start()");

        long time = System.nanoTime() - start;
        start = 0L;

        this.time += (int) TimeUnit.NANOSECONDS.toSeconds(time);
        this.time += (time % 1000000000L) / 1000000000.0;
    }

    public double getElapsedTime() {
        return new BigDecimal(time, new MathContext(5)).doubleValue();
    }
}
