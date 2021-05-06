package me.cjcrafter.pacman;

public class Timer {

    private final double perSecond;
    private final double nanosecondsBetweenTicks;

    private double delta;
    private long lastTime;

    public Timer(double perSecond) {
        this.perSecond = perSecond;
        this.nanosecondsBetweenTicks = 1000000000.0 / perSecond;

        lastTime = now();
    }

    public double getPerSecond() {
        return perSecond;
    }

    public double getNanosecondsBetweenTicks() {
        return nanosecondsBetweenTicks;
    }

    public long nanoBeforeNextTick() {
        long then = lastTime + (long) nanosecondsBetweenTicks;
        return then - now();
    }

    public boolean tick() {
        long now = now();
        delta += (now - lastTime) / nanosecondsBetweenTicks;
        lastTime = now;

        if (delta >= 1.0) {
            delta--;
            return true;
        } else {
            return false;
        }
    }

    private long now() {
        return System.nanoTime();
    }
}
