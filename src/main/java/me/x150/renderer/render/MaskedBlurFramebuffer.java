package me.x150.renderer.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.shader.ShaderManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

/**
 * A framebuffer representing the mask for a gaussian blur shader. Color is ignored, alpha is used as multiplicator for the kernel size.
 */
public class MaskedBlurFramebuffer  extends Framebuffer {
	private static MaskedBlurFramebuffer instance;

	private MaskedBlurFramebuffer(int width, int height) {
		super(false);
		RenderSystem.assertOnRenderThreadOrInit();
		this.resize(width, height);
		this.setClearColor(0f, 0f, 0f, 0f);
	}

	private static MaskedBlurFramebuffer obtain() {
		if (instance == null) {
			instance = new MaskedBlurFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth,
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
		MaskedBlurFramebuffer buffer = obtain();
		if (buffer.textureWidth != mainBuffer.textureWidth || buffer.textureHeight != mainBuffer.textureHeight) {
			buffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight);
		}

		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);

		buffer.beginWrite(true);
		r.run();
		buffer.endWrite();

		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

		mainBuffer.beginWrite(false);
	}

	/**
	 * Uses this framebuffer as a mask for the gaussian blur shader, which blurs the contents of the currently active framebuffer.
	 * <h4>Gaussian blur</h4>
	 * Gaussian blur is distinct from other blur algorithms in that its kernel (the matrix around the current pixel in which other pixels are sampled)
	 * is weighted after the normal distribution. This makes for a much smoother blur compared to other algorithms.
	 * <h4>Parameters</h4>
	 * The kernel size is the overall size of the kernel, how many other pixels around the current pixel are sampled.
	 * The sigma influences how much weight should be placed at the center of the kernel.
	 * For more information and for an example of what the calculation looks like, see <a href="https://www.desmos.com/calculator/xeuj6cqiz6">this desmos graph</a>.
	 * <h4>Tips</h4>
	 * For a nice looking blur, try to configure the sigma such that the y value at the ends of the graph are close to 0.
	 * Note that choosing a high sigma leads to a very even influence of all pixels, which makes the blur effect look similar to a box blur.
	 * @param kernelSizePx Kernel size of the gaussian blur shader
	 * @param sigma Sigma of the gaussian blur shader
	 */
	public static void draw(int kernelSizePx, float sigma) {
		Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
		MaskedBlurFramebuffer buffer = obtain();

		ShaderManager.GAUSSIAN_BLUR.setUniformValue("sigma", sigma);
		ShaderManager.GAUSSIAN_BLUR.setUniformValue("width", kernelSizePx);

		ShaderManager.GAUSSIAN_BLUR.setSamplerUniform("MaskSampler", buffer);

		ShaderManager.GAUSSIAN_BLUR.render(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));

		buffer.clear();

		mainBuffer.beginWrite(false);
	}

	/**
	 * Draws the blur effect without any sort of mask, which blurs the entire framebuffer.
	 * For information for the arguments, see {@link #draw(int, float)}
	 * @param kernelSizePx Kernel size in pixels
	 * @param sigma Sigma value
	 */
	public static void drawNoMask(int kernelSizePx, float sigma) {
		ShaderManager.GAUSSIAN_BLUR_NO_MASK.setUniformValue("sigma", sigma);
		ShaderManager.GAUSSIAN_BLUR_NO_MASK.setUniformValue("width", kernelSizePx);

		ShaderManager.GAUSSIAN_BLUR_NO_MASK.render(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));
	}
}
