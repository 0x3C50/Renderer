package me.x150.renderer.shader;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShaderManager {
	private static final PostEffectProcessor gausNoMask = getShader("gaussian_no_mask");
	private static final PostEffectProcessor gausWithMask = getShader("gaussian");

	public static @NotNull PostEffectProcessor getShader(String shaderName) {
		return Objects.requireNonNull(MinecraftClient.getInstance().getShaderLoader().loadPostEffect(Identifier.of("renderer", shaderName), DefaultFramebufferSet.MAIN_ONLY));
	}

	public static void drawBlur(FrameGraphBuilder fgb, int kernelSizePx, float sigma, GpuTexture maskOrNull) {
		PostEffectProcessor p = maskOrNull == null ? gausNoMask : gausWithMask;
		MinecraftClient client = MinecraftClient.getInstance();
		Framebuffer framebuffer = client.getFramebuffer();
		int i = framebuffer.textureWidth;
		int j = framebuffer.textureHeight;
		PostEffectProcessor.FramebufferSet framebufferSet = PostEffectProcessor.FramebufferSet.singleton(
				PostEffectProcessor.MAIN, fgb.createObjectNode("main", framebuffer)
		);
		p.render(fgb, i, j, framebufferSet, renderPass -> {
			renderPass.setUniform("sigma", sigma);
			renderPass.setUniform("width", (float) kernelSizePx);
			if (maskOrNull != null) renderPass.bindSampler("MaskSampler", maskOrNull);
		});
	}
}
