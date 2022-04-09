/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package me.x150.renderer.event.events;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class BlockRenderingEvent extends RenderEvent {

    final BlockPos bp;
    final BlockState state;

    public BlockRenderingEvent(MatrixStack stack, BlockPos pos, BlockState state) {
        super(stack);
        this.bp = pos;
        this.state = state;
    }

    @SuppressWarnings("unused")
    public BlockPos getPosition() {
        return bp;
    }

    public BlockState getBlockState() {
        return state;
    }
}
