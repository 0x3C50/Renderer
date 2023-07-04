package me.x150.renderer.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.mixinUtil.ShaderEffectDuck;
import me.x150.renderer.shader.ShaderManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

import java.awt.*;
import java.util.Objects;

/**
 * A framebuffer that draws everything in it outlined. Rendered content within this framebuffer isn't rendered as usual, but rather used as a mask. The color of the elements do not matter, but the alpha must be {@code 1} to be counted into the mask.
 */
public class OutlineFramebuffer extends Framebuffer {
	private static OutlineFramebuffer instance;

	private OutlineFramebuffer(int width, int height) {
		super(false);
		RenderSystem.assertOnRenderThreadOrInit();
		this.resize(width, height, true);
		this.setClearColor(0f, 0f, 0f, 0f);
	}

	private static OutlineFramebuffer obtain() {
		if (instance == null) {
			instance = new OutlineFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth,
					MinecraftClient.getInstance().getFramebuffer().textureHeight);
		}
		return instance;
	}

	/**
	 * Draws to this framebuffer. See javadoc of this class for more information.
	 *
	 * @param r The action with rendering calls to write to this framebuffer
	 */
	public static void use(Runnable r) {
		Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
		RenderSystem.assertOnRenderThreadOrInit();
		OutlineFramebuffer buffer = obtain();
		if (buffer.textureWidth != mainBuffer.textureWidth || buffer.textureHeight != mainBuffer.textureHeight) {
			buffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, false);
		}

		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);

		buffer.beginWrite(true);
		r.run();
		buffer.endWrite();

		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

		mainBuffer.beginWrite(false);
	}

	/**
	 * Processes the contents of this framebuffer, then draws them out to the main buffer.
	 *
	 * @param radius       Outline radius. Performance decreases exponentially with a factor of 2, recommended to be kept at 1-4
	 * @param innerColor   Color of the "inner" part of an outline. May be transparent.
	 * @param outlineColor Color of the outline part. May be transparent.
	 */
	public static void draw(float radius, Color outlineColor, Color innerColor) {
		Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
		OutlineFramebuffer buffer = obtain();

		((ShaderEffectDuck) Objects.requireNonNull(
				ShaderManager.OUTLINE_SHADER.getShaderEffect())).renderer$addFakeTarget("inp", buffer);
		// final buffer is written to here, including transparency
		Framebuffer out = ShaderManager.OUTLINE_SHADER.getShaderEffect().getSecondaryTarget("out");

		ShaderManager.OUTLINE_SHADER.setUniformValue("Radius", radius);
		ShaderManager.OUTLINE_SHADER.setUniformValue("OutlineColor", outlineColor.getRed() / 255f,
				outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);
		ShaderManager.OUTLINE_SHADER.setUniformValue("InnerColor", innerColor.getRed() / 255f,
				innerColor.getGreen() / 255f, innerColor.getBlue() / 255f, innerColor.getAlpha() / 255f);
		ShaderManager.OUTLINE_SHADER.setUniformValue("Radius", radius);

		ShaderManager.OUTLINE_SHADER.render(MinecraftClient.getInstance().getTickDelta());

		buffer.clear(false);

		mainBuffer.beginWrite(false);

		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO,
				DstFactor.ONE);
		RenderSystem.backupProjectionMatrix();
		out.draw(out.textureWidth, out.textureHeight, false);
		RenderSystem.restoreProjectionMatrix();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
	}

	/**
	 * Uses this framebuffer, then draws it. This is equivalent to calling {@code use(r)}, followed by {@code draw(radius, outline, inner)}.
	 *
	 * @param r       The action to run within this framebuffer
	 * @param radius  Outline radius. Performance decreases exponentially with a factor of 2, recommended to be kept at 1-4
	 * @param inner   Color of the "inner" part of an outline. May be transparent.
	 * @param outline Color of the outline part. May be transparent.
	 * @see #use(Runnable)
	 */
	public static void useAndDraw(Runnable r, float radius, Color outline, Color inner) {
		use(r);
		draw(radius, outline, inner);
	}
}
