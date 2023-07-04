package me.x150.renderer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.util.BufferUtils;
import me.x150.renderer.util.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Range;
import org.joml.Math;
import org.joml.Matrix4f;

import java.awt.*;

import static me.x150.renderer.render.Renderer3d.getColor;
import static me.x150.renderer.util.RendererUtils.endRender;
import static me.x150.renderer.util.RendererUtils.setupRender;

/**
 * The rendering class for the 2nd dimension, used in the hud renderer or in screens
 */
@SuppressWarnings("unused")
public class Renderer2d {
	/**
	 * Reference to the minecraft client
	 */
	private static final MinecraftClient client = MinecraftClient.getInstance();
	private static final float[][] roundedCache = new float[][]{new float[3], new float[3], new float[3], new float[3],};

	static void beginScissor(double x, double y, double endX, double endY) {
		double width = endX - x;
		double height = endY - y;
		width = Math.max(0, width);
		height = Math.max(0, height);
		float d = (float) client.getWindow().getScaleFactor();
		int ay = (int) ((client.getWindow().getScaledHeight() - (y + height)) * d);
		RenderSystem.enableScissor((int) (x * d), ay, (int) (width * d), (int) (height * d));
	}

	static void endScissor() {
		RenderSystem.disableScissor();
	}

	/**
	 * <p>Renders a texture</p>
	 * <p>Make sure to link your texture using {@link RenderSystem#setShaderTexture(int, Identifier)} before using this</p>
	 *
	 * @param matrices      The context MatrixStack
	 * @param x0            The X coordinate
	 * @param y0            The Y coordinate
	 * @param width         The width of the rendered area
	 * @param height        The height of the rendered area
	 * @param u             The U of the initial texture (0 for none)
	 * @param v             The V of the initial texture (0 for none)
	 * @param regionWidth   The UV Region width of the initial texture (can be width)
	 * @param regionHeight  The UV Region width of the initial texture (can be height)
	 * @param textureWidth  The texture width (can be width)
	 * @param textureHeight The texture height (can be height)
	 */
	public static void renderTexture(MatrixStack matrices, double x0, double y0, double width, double height, float u, float v, double regionWidth, double regionHeight, double textureWidth,
	                                 double textureHeight) {
		double x1 = x0 + width;
		double y1 = y0 + height;
		double z = 0;
		renderTexturedQuad(
				matrices.peek().getPositionMatrix(),
				x0,
				x1,
				y0,
				y1,
				z,
				(u + 0.0F) / (float) textureWidth,
				(u + (float) regionWidth) / (float) textureWidth,
				(v + 0.0F) / (float) textureHeight,
				(v + (float) regionHeight) / (float) textureHeight
		);
	}

	private static void renderTexturedQuad(Matrix4f matrix, double x0, double x1, double y0, double y1, double z, float u0, float u1, float v0, float v1) {
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		buffer.vertex(matrix, (float) x0, (float) y1, (float) z).texture(u0, v1).next();
		buffer.vertex(matrix, (float) x1, (float) y1, (float) z).texture(u1, v1).next();
		buffer.vertex(matrix, (float) x1, (float) y0, (float) z).texture(u1, v0).next();
		buffer.vertex(matrix, (float) x0, (float) y0, (float) z).texture(u0, v0).next();

		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		BufferUtils.draw(buffer);
	}

	/**
	 * <p>Renders a texture</p>
	 * <p>Make sure to link your texture using {@link RenderSystem#setShaderTexture(int, Identifier)} before using this</p>
	 *
	 * @param matrices The context MatrixStack
	 * @param x        The X coordinate
	 * @param y        The Y coordinate
	 * @param width    The width of the texture
	 * @param height   The height of the texture
	 */
	public static void renderTexture(MatrixStack matrices, double x, double y, double width, double height) {
		renderTexture(matrices, x, y, width, height, 0, 0, width, height, width, height);
	}

	/**
	 * <p>Renders a texture</p>
	 * <p>Does the binding for you, call this instead of {@link #renderTexture(MatrixStack, double, double, double, double)} or {@link #renderTexture(MatrixStack, double, double, double, double, float, float, double, double, double, double)} for ease of use</p>
	 *
	 * @param matrices The context MatrixStack
	 * @param texture  The texture to render
	 * @param x        The X coordinate
	 * @param y        The Y coordinate
	 * @param width    The width of the texture
	 * @param height   The height of the texture
	 */
	public static void renderTexture(MatrixStack matrices, Identifier texture, double x, double y, double width, double height) {
		RenderSystem.setShaderTexture(0, texture);
		renderTexture(matrices, x, y, width, height);
	}

	/**
	 * <p>Renders a filled ellipse</p>
	 * <p>Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}</p>
	 *
	 * @param matrices    The context MatrixStack
	 * @param ellipseColor The color of the ellipse
	 * @param originX     The <b>center</b> X coordinate
	 * @param originY     The <b>center</b> Y coordinate
	 * @param radX        Width of the ellipse
	 * @param radY Height of the ellipse
	 * @param segments    How many segments to use to render the ellipse (less = more performance, more = higher quality ellipse)
	 */
	public static void renderEllipse(MatrixStack matrices, Color ellipseColor, double originX, double originY, double radX, double radY, @Range(from = 4, to = 360) int segments) {
		segments = MathHelper.clamp(segments, 4, 360);

		Matrix4f matrix = matrices.peek().getPositionMatrix();

		float[] colorFloat = getColor(ellipseColor);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
		for (int i = 0; i < 360; i += Math.min(360d / segments, 360 - i)) {
			double radians = Math.toRadians(i);
			double sin = Math.sin(radians) * radX;
			double cos = Math.cos(radians) * radY;
			buffer.vertex(matrix, (float) (originX + sin), (float) (originY + cos), 0)
					.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
					.next();
		}
		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		BufferUtils.draw(buffer);
		endRender();
	}

	/**
	 * <p>Renders a filled circle</p>
	 * <p>Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}</p>
	 *
	 * @param matrices MatrixStack
	 * @param circleColor Color of the circle
	 * @param originX Center X coordinate
	 * @param originY Center Y coordinate
	 * @param rad Uniform radius of the circle
	 * @param segments Number of segments that should be rendered. Increasing will decrease speed, decreasing with decrease quality
	 */
	public static void renderCircle(MatrixStack matrices, Color circleColor, double originX, double originY, double rad, @Range(from = 4, to = 360) int segments) {
		renderEllipse(matrices, circleColor, originX, originY, rad, rad, segments);
	}

	/**
	 * <p>Renders an ellipse's outline</p>
	 * <p>Best used inside of {@link MSAAFramebuffer#use(int, Runnable)}</p>
	 *
	 * @param matrices MatrixStack
	 * @param ellipseColor Color of the ellipse
	 * @param originX Center X coordinate
	 * @param originY Center Y coordinate
	 * @param radX Horizontal radius of the ellipse
	 * @param radY Vertical radius of the ellipse
	 * @param width Width of the outline. Should usually be the same as height
	 * @param height Height of the outline. Should usually be the same as width
	 * @param segments Number of segments that should be rendered. Increasing will decrease speed, decreasing with decrease quality
	 */
	public static void renderEllipseOutline(MatrixStack matrices, Color ellipseColor, double originX, double originY, double radX, double radY, @Range(from = 0, to = Long.MAX_VALUE) double width, @Range(from = 0, to = Long.MAX_VALUE) double height, @Range(from=4,to=360) int segments) {
		segments = MathHelper.clamp(segments, 4, 360);
		width = MathHelper.clamp(width, 0, radX);
		height = MathHelper.clamp(height, 0, radY);

		Matrix4f matrix = matrices.peek().getPositionMatrix();

		float[] colorFloat = getColor(ellipseColor);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
		for (int i = 0; i <= segments; i++) {
			double radians = Math.toRadians((double) i / segments * 360d);
			double sin = Math.sin(radians) * (radX-width);
			double cos = Math.cos(radians) * (radY-height);
			double sin1 = Math.sin(radians) * radX;
			double cos1 = Math.cos(radians) * radY;
			buffer.vertex(matrix, (float) (originX + sin), (float) (originY + cos), 0)
					.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
					.next();
			buffer.vertex(matrix, (float) (originX + sin1), (float) (originY + cos1), 0)
					.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
					.next();
		}
		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		BufferUtils.draw(buffer);
		endRender();
	}

	/**
	 * Renders a regular colored quad
	 *
	 * @param matrices The context MatrixStack
	 * @param color    The color of the quad
	 * @param x1       The start X coordinate
	 * @param y1       The start Y coordinate
	 * @param x2       The end X coordinate
	 * @param y2       The end Y coordinate
	 */
	public static void renderQuad(MatrixStack matrices, Color color, double x1, double y1, double x2, double y2) {
		double j;
		if (x1 < x2) {
			j = x1;
			x1 = x2;
			x2 = j;
		}

		if (y1 < y2) {
			j = y1;
			y1 = y2;
			y2 = j;
		}
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		float[] colorFloat = getColor(color);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex(matrix, (float) x1, (float) y2, 0.0F)
				.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
				.next();
		buffer.vertex(matrix, (float) x2, (float) y2, 0.0F)
				.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
				.next();
		buffer.vertex(matrix, (float) x2, (float) y1, 0.0F)
				.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
				.next();
		buffer.vertex(matrix, (float) x1, (float) y1, 0.0F)
				.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
				.next();

		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		BufferUtils.draw(buffer);
		endRender();
	}

	private static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, float fromX, float fromY, float toX, float toY, float radC1, float radC2, float radC3,
	                                              float radC4, float samples) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

		_populateRC(toX - radC4, toY - radC4, radC4, 0);
		_populateRC(toX - radC2, fromY + radC2, radC2, 1);
		_populateRC(fromX + radC1, fromY + radC1, radC1, 2);
		_populateRC(fromX + radC3, toY - radC3, radC3, 3);
		for (int i = 0; i < 4; i++) {
			float[] current = roundedCache[i];
			float rad = current[2];
			for (float r = i * 90f; r <= (i + 1) * 90f; r += 90 / samples) {
				float rad1 = Math.toRadians(r);
				float sin = Math.sin(rad1) * rad;
				float cos = Math.cos(rad1) * rad;

				bufferBuilder.vertex(matrix, current[0] + sin, current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
			}
		}
		BufferUtils.draw(bufferBuilder);
	}

	/**
	 * Renders a rounded rectangle
	 *
	 * @param matrices MatrixStack
	 * @param c        Color of the rect
	 * @param fromX    X coordinate
	 * @param fromY    Y coordinate
	 * @param toX      End X coordinate
	 * @param toY      End Y coordinate
	 * @param radTL    Radius of the top left corner
	 * @param radTR    Radius of the top right corner
	 * @param radBL    Radius of the bottom left corner
	 * @param radBR    Radius of the bottom right corner
	 * @param samples  Samples per corner
	 */
	public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, float radTL, float radTR, float radBL, float radBR, float samples) {
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		float[] color1 = getColor(c);
		float r = color1[0];
		float g = color1[1];
		float b = color1[2];
		float a = color1[3];
		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		renderRoundedQuadInternal(matrix, r, g, b, a, (float) fromX, (float) fromY, (float) toX, (float) toY, radTL,
				radTR, radBL, radBR, samples);
		endRender();
	}

	/**
	 * Renders a rounded rectangle
	 *
	 * @param stack   MatrixStack
	 * @param c       Color of the rect
	 * @param x       X coordinate
	 * @param y       Y coordinate
	 * @param x1      End X coordinate
	 * @param y1      End Y coordinate
	 * @param rad     Radius of the corners
	 * @param samples Samples per corner
	 */
	public static void renderRoundedQuad(MatrixStack stack, Color c, double x, double y, double x1, double y1, float rad, float samples) {
		renderRoundedQuad(stack, c, x, y, x1, y1, rad, rad, rad, rad, samples);
	}

	private static void _populateRC(float a, float b, float c, int i) {
		roundedCache[i][0] = a;
		roundedCache[i][1] = b;
		roundedCache[i][2] = c;
	}

	private static void renderRoundedOutlineInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, float fromX, float fromY, float toX, float toY, float radC1, float radC2, float radC3,
	                                                 float radC4, float width, float samples) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		_populateRC(toX - radC4, toY - radC4, radC4, 0);
		_populateRC(toX - radC2, fromY + radC2, radC2, 1);
		_populateRC(fromX + radC1, fromY + radC1, radC1, 2);
		_populateRC(fromX + radC3, toY - radC3, radC3, 3);
		for (int i = 0; i < 4; i++) {
			float[] current = roundedCache[i];
			float rad = current[2];
			for (float r = i * 90f; r <= (i + 1) * 90f; r += 90 / samples) {
				float rad1 = Math.toRadians(r);
				float sin1 = Math.sin(rad1);
				float sin = sin1 * rad;
				float cos1 = Math.cos(rad1);
				float cos = cos1 * rad;
				bufferBuilder.vertex(matrix, current[0] + sin, current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
				bufferBuilder.vertex(matrix, current[0] + sin + sin1 * width, current[1] + cos + cos1 * width, 0.0F)
						.color(cr, cg, cb, ca)
						.next();
			}
		}
		// last vertex connecting back to start
		float[] current = roundedCache[0];
		float rad = current[2];
		bufferBuilder.vertex(matrix, current[0], current[1] + rad, 0.0F).color(cr, cg, cb, ca).next();
		bufferBuilder.vertex(matrix, current[0], current[1] + rad + width, 0.0F).color(cr, cg, cb, ca).next();
		BufferUtils.draw(bufferBuilder);
	}

	/**
	 * Renders a round outline
	 *
	 * @param matrices     MatrixStack
	 * @param c            Color of the outline
	 * @param fromX        From X coordinate
	 * @param fromY        From Y coordinate
	 * @param toX          To X coordinate
	 * @param toY          To Y coordinate
	 * @param radTL        Radius of the top left corner
	 * @param radTR        Radius of the top right corner
	 * @param radBL        Radius of the bottom left corner
	 * @param radBR        Radius of the bottom right corner
	 * @param outlineWidth Width of the outline
	 * @param samples      Amount of samples to use per corner
	 */
	public static void renderRoundedOutline(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, float radTL, float radTR, float radBL, float radBR, float outlineWidth,
	                                        float samples) {
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		float[] color1 = getColor(c);
		float r = color1[0];
		float g = color1[1];
		float b = color1[2];
		float a = color1[3];
		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		renderRoundedOutlineInternal(matrix, r, g, b, a, (float) fromX, (float) fromY, (float) toX, (float) toY, radTL,
				radTR, radBL, radBR, outlineWidth, samples);
		endRender();
	}

	/**
	 * Renders a round outline
	 *
	 * @param matrices MatrixStack
	 * @param c        Color of the outline
	 * @param fromX    From X coordinate
	 * @param fromY    From Y coordinate
	 * @param toX      To X coordinate
	 * @param toY      To Y coordinate
	 * @param rad      Radius of the corners
	 * @param width    Width of the outline
	 * @param samples  Amount of samples to use per corner
	 */
	public static void renderRoundedOutline(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, float rad, float width, float samples) {
		renderRoundedOutline(matrices, c, fromX, fromY, toX, toY, rad, rad, rad, rad, width, samples);
	}

	/**
	 * Renders a regular line between 2 points
	 *
	 * @param stack The context MatrixStack
	 * @param color The color of the line
	 * @param x     The start X coordinate
	 * @param y     The start Y coordinate
	 * @param x1    The end X coordinate
	 * @param y1    The end Y coordinate
	 */
	public static void renderLine(MatrixStack stack, Color color, double x, double y, double x1, double y1) {
		float[] colorFloat = Colors.intArrayToFloatArray(Colors.ARGBIntToRGBA(color.getRGB()));
		Matrix4f m = stack.peek().getPositionMatrix();

		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(m, (float) x, (float) y, 0f)
				.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
				.next();
		bufferBuilder.vertex(m, (float) x1, (float) y1, 0f)
				.color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3])
				.next();

		setupRender();
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		BufferUtils.draw(bufferBuilder);
		endRender();
	}
}
