/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events.base;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>An event</p>
 */
public class Event {
    /**
     * Whether the event has been cancelled
     */
    @Setter
    @Getter
    boolean cancelled = false;
}
