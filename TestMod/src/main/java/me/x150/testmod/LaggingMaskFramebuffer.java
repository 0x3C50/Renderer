package me.x150.testmod;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

public class LaggingMaskFramebuffer extends Framebuffer {
	private static LaggingMaskFramebuffer instance;

	private LaggingMaskFramebuffer(int width, int height) {
		super(false);
		RenderSystem.assertOnRenderThreadOrInit();
		this.resize(width, height, true);
		this.setClearColor(0f, 0f, 0f, 0f);
	}

	private static LaggingMaskFramebuffer obtain() {
		if (instance == null) {
			instance = new LaggingMaskFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth,
					MinecraftClient.getInstance().getFramebuffer().textureHeight);
		}
		return instance;
	}

	public static void use(Runnable r) {
		Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
		RenderSystem.assertOnRenderThreadOrInit();
		LaggingMaskFramebuffer buffer = obtain();
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

	public static void draw() {
		Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
		LaggingMaskFramebuffer buffer = obtain();

		TestMod.mse.setSamplerUniform("Mask", buffer);
		TestMod.mse.setUniformValue("time", System.nanoTime() / 1e6f / 1000f);

		TestMod.mse.render(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));

		buffer.clear(false);

		mainBuffer.beginWrite(false);
	}
}
