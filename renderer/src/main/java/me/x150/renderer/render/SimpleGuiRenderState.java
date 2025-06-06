package me.x150.renderer.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.x150.renderer.mixin.DrawContextAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

import java.util.function.BiConsumer;

public record SimpleGuiRenderState(
		RenderPipeline pipeline,
		TextureSetup textureSetup,
		ScreenRect scissorArea,
		ScreenRect bounds,
		BiConsumer<VertexConsumer, Float> vertices
) implements SimpleGuiElementRenderState {
	public SimpleGuiRenderState(RenderPipeline pipeline, TextureSetup ts, DrawContext context, ScreenRect bounds, BiConsumer<VertexConsumer, Float> vertices) {
		this(pipeline, ts, ((DrawContextAccessor) context).getScissorStack().peekLast(), bounds, vertices);
	}
	@Override
	public void setupVertices(VertexConsumer vertices, float depth) {
		this.vertices.accept(vertices, depth);
	}
}
