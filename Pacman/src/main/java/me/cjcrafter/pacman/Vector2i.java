package me.cjcrafter.pacman;

import java.util.Objects;

public class Vector2i implements Cloneable {

    private int x;
    private int y;

    public Vector2i() {
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Vector2i add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2i add(Vector2i other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public Vector2i subtract(Vector2i other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public Vector2i multiply(int n) {
        this.x *= n;
        this.y *= n;
        return this;
    }

    public Vector2i multiply(int x, int y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2i divide(int n) {
        this.x /= n;
        this.y /= n;
        return this;
    }

    public Vector2i modulus(int n) {
        this.x %= n;
        this.y %= n;
        return this;
    }

    public int dx(Vector2i other) {
        return other.x - this.x;
    }

    public int dy(Vector2i other) {
        return other.y - this.y;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public int lengthSquared() {
        return square(x) + square(y);
    }

    public double distance(Vector2i other) {
        return Math.sqrt(distanceSquared(other));
    }

    public int distanceSquared(Vector2i other) {
        return square(x - other.x) + square(y - other.y);
    }

    public int dot(Vector2i other) {
        return this.x * other.x + this.y * other.y;
    }

    public double angle(Vector2i other) {
        return Math.acos(dot(other) / (this.length() * other.length()));
    }

    public Vector2d toDouble() {
        return new Vector2d(x, y);
    }

    @Override
    public Vector2i clone() {
        try {
            return (Vector2i) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2i vector2d = (Vector2i) o;
        return x == vector2d.x && y == vector2d.y;
    }

    public boolean equals(int x, int y) {
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

    private static int square(int a) {
        return a * a;
    }
}