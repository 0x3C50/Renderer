package me.x150.testmod;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

public class LaggingMaskFramebuffer extends Framebuffer {
	private static LaggingMaskFramebuffer instance;
	private final int depth;

	private LaggingMaskFramebuffer(int width, int height, int depth) {
		super(true);
		this.depth = depth;
		RenderSystem.assertOnRenderThreadOrInit();
		this.resize(width, height, true);
		this.setClearColor(0f, 0f, 0f, 0f);
	}

	private void setTexFilter(int texFilter, boolean force) {
		RenderSystem.assertOnRenderThreadOrInit();
		if (force || texFilter != this.texFilter) {
			this.texFilter = texFilter;
			GlStateManager._bindTexture(this.colorAttachment);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, texFilter);
			GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, texFilter);
			GlStateManager._bindTexture(0);
		}
	}

	@Override
	public void initFbo(int width, int height, boolean getError) {
		RenderSystem.assertOnRenderThreadOrInit();
		int i = RenderSystem.maxSupportedTextureSize();
		if (width <= 0 || width > i || height <= 0 || height > i) {
			throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
		}
		this.viewportWidth = width;
		this.viewportHeight = height;
		this.textureWidth = width;
		this.textureHeight = height;
		this.fbo = GlStateManager.glGenFramebuffers();
		this.colorAttachment = TextureUtil.generateTextureId();
		if (this.useDepthAttachment) {
			this.depthAttachment = depth;
		}
		this.setTexFilter(GlConst.GL_NEAREST, true);
		GlStateManager._bindTexture(this.colorAttachment);
		GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_S, GlConst.GL_CLAMP_TO_EDGE);
		GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_WRAP_T, GlConst.GL_CLAMP_TO_EDGE);
		GlStateManager._texImage2D(GlConst.GL_TEXTURE_2D, 0, GlConst.GL_RGBA8, this.textureWidth, this.textureHeight, 0, GlConst.GL_RGBA, GlConst.GL_UNSIGNED_BYTE, null);
		GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.fbo);
		GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GlConst.GL_TEXTURE_2D, this.colorAttachment, 0);
		if (this.useDepthAttachment) {
			GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D, this.depthAttachment, 0);
		}
		this.checkFramebufferStatus();
		this.clear(getError);
		this.endRead();
	}

	@Override
	public void delete() {
		RenderSystem.assertOnRenderThreadOrInit();
		this.endRead();
		this.endWrite();
		this.depthAttachment = -1; // dont free it
		if (this.colorAttachment > -1) {
			TextureUtil.releaseTextureId(this.colorAttachment);
			this.colorAttachment = -1;
		}
		if (this.fbo > -1) {
			GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
			GlStateManager._glDeleteFramebuffers(this.fbo);
			this.fbo = -1;
		}
	}

	public static LaggingMaskFramebuffer obtain() {
		if (instance == null) {
			Framebuffer fb = MinecraftClient.getInstance().getFramebuffer();
			instance = new LaggingMaskFramebuffer(fb.textureWidth,
					fb.textureHeight, fb.getDepthAttachment());
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
