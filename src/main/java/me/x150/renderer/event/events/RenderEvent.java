/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import me.x150.renderer.event.events.base.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent extends Event {

    final MatrixStack stack;

    public RenderEvent(MatrixStack stack) {
        this.stack = stack;
    }

    public MatrixStack getStack() {
        return stack;
    }
}
