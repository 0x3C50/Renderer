package me.x150.testmod.client;


import me.x150.renderer.event.RenderEvents;
import me.x150.testmod.Handler;
import net.fabricmc.api.ClientModInitializer;

public class TestModClient implements ClientModInitializer {
	/**
	 * Runs the mod initializer on the client environment.
	 */
	@Override
	public void onInitializeClient() {
		RenderEvents.HUD.register(Handler::hud);
		RenderEvents.WORLD.register(Handler::world);
	}
}
