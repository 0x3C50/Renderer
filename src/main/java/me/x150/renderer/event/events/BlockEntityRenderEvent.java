/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A block entity was rendered
 */
@SuppressWarnings("unused")
public class BlockEntityRenderEvent extends RenderEvent {

    final BlockEntity entity;

    /**
     * Constructs a new event
     *
     * @param shift  The shift
     * @param stack  The context matrixstack
     * @param entity The entity that was rendered
     */
    public BlockEntityRenderEvent(MatrixStack stack, BlockEntity entity) {
        super(stack);
        this.entity = entity;
    }

    /**
     * Returns the entity that was rendered
     *
     * @return The entity
     */
    public BlockEntity getBlockEntity() {
        return entity;
    }
}
