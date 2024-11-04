package me.x150.renderer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.mixin.GameRendererAccessor;
import me.x150.renderer.mixin.PostEffectProcessorMixin;
import me.x150.renderer.shader.ShaderManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;

import java.awt.*;
import java.util.List;

/**
 * A framebuffer that draws everything in it outlined. Rendered content within this framebuffer isn't rendered as usual, but rather used as a mask. The color of the elements do not matter, but the alpha must be {@code 1} to be counted into the mask.
 * @deprecated Out of scope (too specific) for this library. You should probably copy and modify this to your needs.
 */
@Deprecated(since = "1.2.4")
public class OutlineFramebuffer extends Framebuffer {
	private static OutlineFramebuffer instance;

	private OutlineFramebuffer(int width, int height) {
		super(false);
		RenderSystem.assertOnRenderThreadOrInit();
		this.resize(width, height);
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
			buffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight);
		}

//		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);

		buffer.beginWrite(false);
		r.run();
		buffer.endWrite();

//		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

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

		PostEffectProcessor outlineShader = ShaderManager.getOutlineShader();
		List<PostEffectPass> allPasses = ((PostEffectProcessorMixin) outlineShader).getPasses();
		PostEffectPass firstPass = allPasses.getFirst();
		ShaderProgram firstPassProgram = firstPass.getProgram();

		firstPassProgram.addSamplerTexture("MaskSampler", buffer.colorAttachment);
		firstPassProgram.getUniform("Radius").set(radius);
		firstPassProgram.getUniform("OutlineColor").set(outlineColor.getRed() / 255f,
				outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);
		firstPassProgram.getUniform("InnerColor").set(innerColor.getRed() / 255f,
				innerColor.getGreen() / 255f, innerColor.getBlue() / 255f, innerColor.getAlpha() / 255f);

		RenderSystem.depthMask(false); // DO NOT write to depth buffer, else the depth buffer gets fucked
		outlineShader.render(
				mainBuffer, ((GameRendererAccessor) MinecraftClient.getInstance().gameRenderer).getPool()
		);
		RenderSystem.depthMask(true);

		buffer.clear();

		mainBuffer.beginWrite(false);
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
