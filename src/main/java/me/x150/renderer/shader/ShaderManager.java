package me.x150.renderer.shader;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.ManagedCoreShader;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

public class ShaderManager {
	public static final ManagedShaderEffect OUTLINE_SHADER = ShaderEffectManager.getInstance()
			.manage(Identifier.of("renderer", "shaders/post/outline.json"));
	public static final ManagedCoreShader POSITION_TEX_COLOR_NORMAL = ShaderEffectManager.getInstance()
			.manageCoreShader(Identifier.of("renderer", "position_tex_color_normal"), VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
	public static final ManagedShaderEffect GAUSSIAN_BLUR = ShaderEffectManager.getInstance()
			.manage(Identifier.of("renderer", "shaders/post/gaussian.json"));
	public static final ManagedShaderEffect GAUSSIAN_BLUR_NO_MASK = ShaderEffectManager.getInstance()
			.manage(Identifier.of("renderer", "shaders/post/gaussian_no_mask.json"));

	public static void doInit() {
		// NOOP to get class to load on demand
	}
}
