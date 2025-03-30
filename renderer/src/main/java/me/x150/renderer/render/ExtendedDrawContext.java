package me.x150.renderer.render;

import me.x150.renderer.generated.Emitter;
import me.x150.renderer.mixin.DrawContextAccessor;
import me.x150.renderer.util.Color;
import me.x150.renderer.util.DirectVertexConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ExtendedDrawContext {
	public static void drawEllipse(DrawContext instance, float x, float y, float width, float height, Color color) {
		VertexConsumerProvider.Immediate vcp = ((DrawContextAccessor) instance).getVertexConsumers();
		VertexConsumer ellipse = vcp.getBuffer(CustomRenderLayers.ELLIPSE_QUADS);
		MatrixStack.Entry transform = instance.getMatrices().peek();

		float r = color.red();
		float g = color.green();
		float b = color.blue();
		float a = color.alpha();

		{
			// Vertices: 0-1-2-3
			ellipse.vertex(transform, x, y + height, (float) 0).texture(0, 0).color(r, g, b, a);
			ellipse.vertex(transform, x + width, y + height, (float) 0).texture(1, 0).color(r, g, b, a);
			ellipse.vertex(transform, x + width, y, (float) 0).texture(1, 1).color(r, g, b, a);
			ellipse.vertex(transform, x, y, (float) 0).texture(0, 1).color(r, g, b, a);
		}
	}

	public static void drawRR(DrawContext instance, float x, float y, float width, float height, Vector4f roundness, Color color) {
		VertexConsumerProvider.Immediate vcp = ((DrawContextAccessor) instance).getVertexConsumers();
		VertexConsumer ellipse = vcp.getBuffer(CustomRenderLayers.ROUNDED_RECT.apply(roundness));
		MatrixStack.Entry transform = instance.getMatrices().peek();

		BufferBuilder bruh = (BufferBuilder) ellipse;
		//		BufferBuilderAccessor bba = (BufferBuilderAccessor) bruh;

		float r = color.red();
		float g = color.green();
		float b = color.blue();
		float a = color.alpha();

		DirectVertexConsumer dvc = new DirectVertexConsumer(bruh, false);

		{
			// Vertices: 0-1-2-3
			dvc.vertex(transform, x, y + height, (float) 0).texture(0, 0).texture(width, height).color(r, g, b, a);
			dvc.vertex(transform, x + width, y + height, (float) 0).texture(width, 0).texture(width, height).color(r, g, b, a);
			dvc.vertex(transform, x + width, y, (float) 0).texture(width, height).texture(width, height).color(r, g, b, a);
			dvc.vertex(transform, x, y, (float) 0).texture(0, height).texture(width, height).color(r, g, b, a);
		}
	}

	public static void drawLine(DrawContext instance, float x, float y, float toX, float toY, float thickness, Color color) {
		VertexConsumerProvider.Immediate vcp = ((DrawContextAccessor) instance).getVertexConsumers();
		VertexConsumer ellipse = vcp.getBuffer(CustomRenderLayers.getLines(thickness, false));
		MatrixStack.Entry transform = instance.getMatrices().peek();

		float r = color.red();
		float g = color.green();
		float b = color.blue();
		float a = color.alpha();

		Vector2f direction = new Vector2f(toX, toY).sub(x, y).normalize();

		Emitter._emit_line__2xposition_color_normal(transform, ellipse, x, y, 0, r, g, b, a, direction.x, direction.y, 0, toX, toY, 0, r, g, b, a, direction.x, direction.y, 0);
	}
}
