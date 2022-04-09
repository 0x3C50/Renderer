/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events.base;

import me.x150.renderer.event.Shift;

/**
 * An event
 */
public class Event {

    boolean cancelled = false;

    /**
     * Returns if the event was cancelled by a handler
     * @return If the event was cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Marks the event as cancelled or not
     * @param cancelled Whether the event is cancelled
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
