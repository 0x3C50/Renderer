/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import lombok.Getter;
import me.x150.renderer.event.events.base.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent extends Event {

    /**
     * The context MatrixStack
     */
    @Getter
    final MatrixStack stack;

    /**
     * Constructs a new event
     *
     * @param stack The context MatrixStack
     */
    public RenderEvent(MatrixStack stack) {
        this.stack = stack;
    }
}
