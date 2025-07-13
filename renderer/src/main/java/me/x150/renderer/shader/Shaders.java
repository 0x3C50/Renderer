package me.x150.renderer.shader;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.mixin.PostEffectPassAccessor;
import me.x150.renderer.mixin.PostEffectProcessorAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// FIXME 13 Juli 2025 23:46: reloading
public class Shaders {
	private static final GpuBuffer uniformsBuffer = RenderSystem.getDevice().createBuffer(() -> "renderer blurconfig", GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE, 8);

	private static final Identifier the = Identifier.of("renderer", "mask");
	private static final PostEffectProcessor gausNoMask = patchUniforms(getShader("gaussian_no_mask"));
	private static final PostEffectProcessor gausWithMask = patchUniforms(getShader("gaussian"));

	private static @NotNull PostEffectProcessor getShader(String shaderName) {
		return Objects.requireNonNull(MinecraftClient.getInstance().getShaderLoader().loadPostEffect(Identifier.of("renderer", shaderName), Set.of(DefaultFramebufferSet.MAIN, the)));
	}

	private static PostEffectProcessor patchUniforms(PostEffectProcessor p) {
		List<PostEffectPass> passes = ((PostEffectProcessorAccessor) p).getPasses();
		for (PostEffectPass pass : passes) {
			Map<String, GpuBuffer> uniforms = ((PostEffectPassAccessor) pass).getUniformBuffers();
			GpuBuffer bc = uniforms.get("BlurConfig");
			bc.close();
			uniforms.put("BlurConfig", uniformsBuffer);
		}
		return p;
	}

	public static void drawBlur(FrameGraphBuilder fgb, int kernelSizePx, float sigma, Framebuffer maskOrNull) {
		PostEffectProcessor p = maskOrNull == null ? gausNoMask : gausWithMask;
		MinecraftClient client = MinecraftClient.getInstance();
		Framebuffer oF = client.getFramebuffer();
		int i = oF.textureWidth;
		int j = oF.textureHeight;
		PostEffectProcessor.FramebufferSet framebufferSet = new PostEffectProcessor.FramebufferSet() {
			private Handle<Framebuffer> framebuffer = fgb.createObjectNode("main", oF);
			private Handle<Framebuffer> mask = maskOrNull == null ? null : fgb.createObjectNode("mask", maskOrNull);

			@Override
			public void set(Identifier id, Handle<Framebuffer> framebuffer) {
				if (id.equals(PostEffectProcessor.MAIN)) {
					this.framebuffer = framebuffer;
				} else if (id.equals(the)) {
					this.mask = framebuffer;
				} else {
					throw new IllegalArgumentException("No target with id " + id);
				}
			}

			@Nullable
			@Override
			public Handle<Framebuffer> get(Identifier id) {
				if (id.equals(PostEffectProcessor.MAIN)) return this.framebuffer;
				else if (id.equals(the)) return this.mask;
				return null;
			}
		};

		try (GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(Shaders.uniformsBuffer, false, true)) {
			Std140Builder std140Builder = Std140Builder.intoBuffer(mappedView.data());
			std140Builder.putFloat(kernelSizePx).putFloat(sigma);
		}

		p.render(fgb, i, j, framebufferSet);
	}
}
