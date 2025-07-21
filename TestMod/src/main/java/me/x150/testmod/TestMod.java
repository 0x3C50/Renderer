package me.x150.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class TestMod implements ModInitializer {

	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("the").executes(context -> {
				context.getSource().sendFeedback(Text.literal("yeah"));
				// never ever do this ever
				Thread.ofVirtual().start(() -> {
					MinecraftClient.getInstance().execute(() -> {
						MinecraftClient.getInstance().setScreen(new TestScreen());
					});
				});
				return 1;
			}));
		});
	}
}
