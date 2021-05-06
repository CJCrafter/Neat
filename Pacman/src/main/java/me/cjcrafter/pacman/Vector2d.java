package me.cjcrafter.pacman;

import java.util.Objects;

public class Vector2d implements Cloneable {

    private double x;
    private double y;

    public Vector2d() {
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2d add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2d add(Vector2d other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2d subtract(Vector2d other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2d multiply(double n) {
        this.x *= n;
        this.y *= n;
        return this;
    }

    public Vector2d divide(double n) {
        this.x /= n;
        this.y /= n;
        return this;
    }

    public Vector2d modulus(double n) {
        this.x %= n;
        this.y %= n;
        return this;
    }

    public Vector2d zero() {
        this.x = 0.0;
        this.y = 0.0;
        return this;
    }

    public double dx(Vector2d other) {
        return other.x - this.x;
    }

    public double dy(Vector2d other) {
        return other.y - this.y;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return square(x) + square(y);
    }

    public double distance(Vector2d other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double distanceSquared(Vector2d other) {
        return square(x - other.x) + square(y - other.y);
    }

    public double dot(Vector2d other) {
        return this.x * other.x + this.y * other.y;
    }

    public double angle(Vector2d other) {
        return Math.acos(dot(other) / (this.length() * other.length()));
    }

    @Override
    public Vector2d clone() {
        try {
            return (Vector2d) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2d vector2d = (Vector2d) o;
        return x == vector2d.x && y == vector2d.y;
    }

    public boolean equals(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Vector2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    private static double square(double a) {
        return a * a;
    }
}