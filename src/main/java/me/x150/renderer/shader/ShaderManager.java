package me.x150.renderer.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShaderManager {
	public static final ShaderProgramKey SPK_POSITION_TEX_COLOR_NORMAL = new ShaderProgramKey(Identifier.of("renderer", "core/position_tex_color_normal"), VertexFormats.POSITION_TEXTURE_COLOR_NORMAL, Defines.EMPTY);

	public static @NotNull PostEffectProcessor getGaussianNoMaskShader() {
		return getShader("gaussian_no_mask");
	}

	public static @NotNull PostEffectProcessor getGaussianShader() {
		return getShader("gaussian");
	}

	public static @NotNull PostEffectProcessor getOutlineShader() {
		return getShader("outline");
	}

	public static @NotNull PostEffectProcessor getShader(String shaderName) {
		return Objects.requireNonNull(MinecraftClient.getInstance().getShaderLoader().loadPostEffect(Identifier.of("renderer", shaderName), DefaultFramebufferSet.MAIN_ONLY));
	}
}
