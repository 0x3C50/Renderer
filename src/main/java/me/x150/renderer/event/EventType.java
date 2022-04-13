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
     * <p>A block has been rendered</p>
     * <p>Gets called once to cache the block, then only when the block is regenerated. Does <b>NOT</b> get called each frame</p>
     */
    BLOCK_RENDER,
    /**
     * <p>The in game hud has been rendered</p>
     * <p>Gets called only when in game, and only if the F1 "Hide hud" flag is not set</p>
     */
    HUD_RENDER,
    /**
     * <p>The world has been rendered</p>
     * <p>Gets called only when in game</p>
     */
    WORLD_RENDER,
    /**
     * <p>A screen has been rendered</p>
     * <p>Gets called only when there is a screen present, and only if there is no overlay</p>
     */
    SCREEN_RENDER
}
