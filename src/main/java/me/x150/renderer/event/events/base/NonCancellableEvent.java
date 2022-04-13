/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events.base;

/**
 * <p>An event that cannot be cancelled</p>
 */
public class NonCancellableEvent extends Event {

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        throw new IllegalStateException("Event cannot be cancelled.");
    }
}
