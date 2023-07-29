package me.x150.renderer.shader;

import ladysnake.satin.api.managed.ManagedCoreShader;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderManager {
	public static void register() {

	}
	public static final ManagedShaderEffect OUTLINE_SHADER = ShaderEffectManager.getInstance()
			.manage(new Identifier("renderer", String.format("shaders/post/%s.json", "outline")));
	public static final ManagedCoreShader OBJ_SHADER = ShaderEffectManager.getInstance()
			.manageCoreShader(new Identifier("renderer", "obj"), VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
}
