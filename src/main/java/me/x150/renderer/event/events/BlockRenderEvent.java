/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

/**
 * A block has been rendered
 */
public class BlockRenderEvent extends RenderEvent {

    /**
     * The BlockPos of the rendered block
     */
    @Getter
    final BlockPos position;
    /**
     * The BlockState of the rendered block
     */
    @Getter
    final BlockState state;

    /**
     * Constructs a new event
     *
     * @param stack The context MatrixStack
     * @param pos   The block's position
     * @param state The block's BlockState
     */
    public BlockRenderEvent(MatrixStack stack, BlockPos pos, BlockState state) {
        super(stack);
        this.position = pos;
        this.state = state;
    }
}
