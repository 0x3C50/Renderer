/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event;

/**
 * Event types
 */
public enum EventType {
    /**
     * An entity has been rendered
     */
    ENTITY_RENDER,
    /**
     * A block entity has been rendered
     */
    BLOCK_ENTITY_RENDER,
    /**
     * A block has been rendered<br>
     * Gets called once to cache the block, then only when the block is regenerated. Does <b>NOT</b> get called each frame
     */
    BLOCK_RENDER,
    /**
     * The in game hud has been rendered<br>
     * Gets called only when in game, and only if the F1 "Hide hud" flag is not set
     */
    HUD_RENDER,
    /**
     * The world has been rendered<br>
     * Gets called only when in game
     */
    WORLD_RENDER,
    /**
     * A screen has been rendered<br>
     * Gets called only when there is a screen present, and only if there is no overlay
     */
    SCREEN_RENDER
}
