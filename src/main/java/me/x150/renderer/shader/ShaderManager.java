package me.x150.renderer.shader;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.util.Identifier;

public class ShaderManager {
	public static final ManagedShaderEffect OUTLINE_SHADER = ShaderEffectManager.getInstance()
			.manage(new Identifier("renderer", String.format("shaders/post/%s.json", "outline")));
}
