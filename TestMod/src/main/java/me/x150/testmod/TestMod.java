package me.x150.testmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

public class TestMod implements ModInitializer {
	public static ManagedShaderEffect mse;
	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		mse = ShaderEffectManager.getInstance().manage(Identifier.of("testmod", "shaders/post/bruhhrubruh.json"));
	}
}
