package me.x150.renderer.shader;

import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderManager {
	public static final ManagedShaderEffect OUTLINE_SHADER = ShaderEffectManager.getInstance()
			.manage(new Identifier("renderer", "shaders/post/outline.json"));
	public static final ManagedCoreShader POSITION_TEX_COLOR_NORMAL = ShaderEffectManager.getInstance()
			.manageCoreShader(new Identifier("renderer", "position_tex_color_normal"), VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

	public static void doInit() {
		// NOOP to get class to load on demand
	}
}
