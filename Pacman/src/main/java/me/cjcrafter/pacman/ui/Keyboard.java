package me.cjcrafter.pacman.ui;

import me.cjcrafter.pacman.Direction;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

    private Direction dir;

    public Direction peek() {
        return dir;
    }

    public Direction pop() {
        Direction dir = this.dir;
        this.dir = null;
        return dir;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> dir = Direction.UP;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> dir = Direction.DOWN;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> dir = Direction.LEFT;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> dir = Direction.RIGHT;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
