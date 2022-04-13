/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import lombok.Getter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A block entity was rendered
 */
@SuppressWarnings("unused")
public class BlockEntityRenderEvent extends RenderEvent {

    /**
     * The BlockEntity which has been rendered
     */
    @Getter
    final BlockEntity entity;

    /**
     * Constructs a new event
     *
     * @param stack  The context MatrixStack
     * @param entity The BlockEntity that was rendered
     */
    public BlockEntityRenderEvent(MatrixStack stack, BlockEntity entity) {
        super(stack);
        this.entity = entity;
    }
}
