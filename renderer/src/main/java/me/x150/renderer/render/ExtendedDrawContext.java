package me.x150.renderer.render;

import me.x150.renderer.generated.Emitter;
import me.x150.renderer.mixin.DrawContextAccessor;
import me.x150.renderer.util.Color;
import me.x150.renderer.util.DirectVertexConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureSetup;
import org.joml.*;

import java.lang.Math;

/**
 * More utilities for drawing on the hud
 */
public class ExtendedDrawContext {

	private static ScreenRect createBounds(DrawContext c, float x, float y, float w, float h) {
		Matrix3x2fStack mat = c.getMatrices();
		DrawContext.ScissorStack ss = ((DrawContextAccessor) c).getScissorStack();
		ScreenRect scissor = ss.peekLast();
		ScreenRect screenRect = new ScreenRect((int) Math.floor(x), (int) Math.floor(y), (int) Math.ceil(w), (int) Math.ceil(h)).transformEachVertex(mat);
		return scissor != null ? scissor.intersection(screenRect) : screenRect;
	}

	/**
	 * Draws an ellipse. The radius is dependent on width and height, the ellipse might be stretched depending on
	 * width and height. Color is uniform.
	 * @param instance DrawContext to draw with
	 * @param x Top left X coordinate
	 * @param y Top left Y coordinate
	 * @param width Width
	 * @param height Height
	 * @param color Color
	 */
	public static void drawEllipse(DrawContext instance, float x, float y, float width, float height, Color color) {

		Matrix3x2f matrices = new Matrix3x2f(instance.getMatrices());
		SimpleGuiElementRenderState state = new SimpleGuiRenderState(
				CustomRenderLayers.ELLIPSE_PIPELINE, TextureSetup.empty(), instance,
				createBounds(instance, x, y, width, height),
				(bruh, aFloat) -> {
					float r = color.red();
					float g = color.green();
					float b = color.blue();
					float a = color.alpha();

					DirectVertexConsumer ellipse = new DirectVertexConsumer((BufferBuilder) bruh, false);

					{
						// Vertices: 0-1-2-3
						//@formatter:off
						ellipse.vertex(matrices, x, y + height, aFloat).texture(0, 0).color(r, g, b, a);
						ellipse.vertex(matrices, x + width, y + height, aFloat).texture(1, 0).color(r, g, b, a);
						ellipse.vertex(matrices, x + width, y, aFloat).texture(1, 1).color(r, g, b, a);
						ellipse.vertex(matrices, x, y, aFloat).texture(0, 1).color(r, g, b, a);
						//@formatter:on
					}
				}
		);
		((DrawContextAccessor) instance).getState().addSimpleElement(state);

	}

	/**
	 * Draw a rounded rectangle. Color is uniform.
	 * @param instance DrawContext to draw with
	 * @param x Top left X coordinate
	 * @param y Top left Y coordinate
	 * @param width Width
	 * @param height Height
	 * @param roundness Vec4 describing roundness of each corner, in logical pixels. Order is {@code (TR, BR, TL, BL)}.
	 * @param color Color
	 */
	public static void drawRoundedRect(DrawContext instance, float x, float y, float width, float height, Vector4f roundness, Color color) {

		Matrix3x2f matrices = new Matrix3x2f(instance.getMatrices());
		SimpleGuiElementRenderState state = new SimpleGuiRenderState(
				CustomRenderLayers.RR_PIPELINE, TextureSetup.empty(), instance,
				createBounds(instance, x, y, width, height),
				(bruh, aFloat) -> {
					float r = color.red();
					float g = color.green();
					float b = color.blue();
					float a = color.alpha();

					DirectVertexConsumer dvc = new DirectVertexConsumer((BufferBuilder) bruh, false);

					{
						// Vertices: 0-1-2-3
						//@formatter:off
dvc.vertex(matrices, x, y + height, aFloat)			.texture(0, 0)		 .texture(width, height).texture(roundness.x, roundness.y).texture(roundness.z, roundness.w).color(r, g, b, a);
dvc.vertex(matrices, x + width, y + height, aFloat).texture(width, 0)	 .texture(width, height).texture(roundness.x, roundness.y).texture(roundness.z, roundness.w).color(r, g, b, a);
dvc.vertex(matrices, x + width, y, aFloat)			.texture(width, height).texture(width, height).texture(roundness.x, roundness.y).texture(roundness.z, roundness.w).color(r, g, b, a);
dvc.vertex(matrices, x, y, aFloat)						.texture(0, height)	 .texture(width, height).texture(roundness.x, roundness.y).texture(roundness.z, roundness.w).color(r, g, b, a);
						//@formatter:on
					}
				}
		);
		((DrawContextAccessor) instance).getState().addSimpleElement(state);
	}

	/**
	 * Draws a line. Color is uniform.
	 * @param instance DrawContext to draw with
	 * @param x Start X
	 * @param y Start Y
	 * @param toX End X
	 * @param toY End Y
	 * @param thickness Thickness of the line, or 0 for default
	 * @param color Color
	 */
	public static void drawLine(DrawContext instance, float x, float y, float toX, float toY, float thickness, Color color) {
		Matrix3x2f matrices = new Matrix3x2f(instance.getMatrices());
		Matrix4f posTransform = new Matrix4f();
		posTransform.mul(matrices);
		Matrix3f normTransform = new Matrix3f();
		posTransform.get3x3(normTransform);

		float width = Math.abs(toX - x);
		float height = Math.abs(toY - y);
		float startX = Math.min(x, toX);
		float startY = Math.min(y, toY);
		SimpleGuiElementRenderState state = new SimpleGuiRenderState(
				CustomRenderLayers.LINES_DEPTH_PIPELINE, TextureSetup.empty(), instance,
				createBounds(instance, startX, startY, width, height),
				(bruh, aFloat) -> {
					float r = color.red();
					float g = color.green();
					float b = color.blue();
					float a = color.alpha();

					Vector2f direction = new Vector2f(toX, toY).sub(x, y).normalize();

					Emitter._emit_line__2xposition_color_normal(posTransform, normTransform, bruh,
							x, y, aFloat, r, g, b, a, direction.x, direction.y, 0,
							toX, toY, aFloat, r, g, b, a, direction.x, direction.y, 0);
				}
		);
		((DrawContextAccessor) instance).getState().addSimpleElement(state);
	}
}
