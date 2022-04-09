/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An event listener method
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {
    /**
     * The type to subscribe to
     *
     * @return The event type
     */
    EventType type();

    /**
     * The desired shift of the event
     *
     * @return The shift
     */
    Shift shift();
}
