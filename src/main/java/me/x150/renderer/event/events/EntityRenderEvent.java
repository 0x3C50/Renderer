/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/**
 * An entity has been rendered
 */
public class EntityRenderEvent extends RenderEvent {

    /**
     * The rendered entity
     */
    @Getter
    final Entity target;

    /**
     * Constructs a new event
     *
     * @param stack  The context MatrixStack
     * @param entity The entity that has been rendered
     */
    public EntityRenderEvent(MatrixStack stack, Entity entity) {
        super(stack);
        this.target = entity;
    }
}
