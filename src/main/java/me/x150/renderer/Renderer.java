package me.x150.renderer;

import me.x150.renderer.shader.ShaderManager;
import net.fabricmc.api.ModInitializer;

public class Renderer implements ModInitializer {
	@Override
	public void onInitialize() {
		ShaderManager.doInit();
	}
}
