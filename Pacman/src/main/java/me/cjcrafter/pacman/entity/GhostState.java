package me.cjcrafter.pacman.entity;

import me.cjcrafter.pacman.Vector2i;

public enum GhostState {

    CHASE(0, 0, true, 2),
    SCATTER(0, 0, true, 2),
    FRIGHTENED(0, 1, false, 2),
    EATEN(2, 1, true, 1);

    private final Vector2i spriteOffset;
    private final boolean directional;
    private final int animationFrames;

    GhostState(int x, int y, boolean directional, int animationFrames) {
        this.spriteOffset = new Vector2i(x, y);
        this.directional = directional;
        this.animationFrames = animationFrames;
    }

    public Vector2i getSpriteOffset() {
        return spriteOffset.clone();
    }

    public boolean isDirectional() {
        return directional;
    }

    public int getAnimationFrames() {
        return animationFrames;
    }
}
