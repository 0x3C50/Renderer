/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class EntityRenderEvent extends RenderEvent {

    final Entity target;

    public EntityRenderEvent(MatrixStack stack, Entity e) {
        super(stack);
        this.target = e;
    }

    public Entity getEntity() {
        return target;
    }
}
