package me.cjcrafter.snake.board;

import java.util.Objects;

public class Vector2d implements Cloneable {

    private int x;
    private int y;

    public Vector2d(int x, int y) {
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

    public Vector2d add(int x, int y) {
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

    public int dx(Vector2d other) {
        return other.x - this.x;
    }

    public int dy(Vector2d other) {
        return other.y - this.y;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return x * x + y * y;
    }

    public int dot(Vector2d other) {
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
}
