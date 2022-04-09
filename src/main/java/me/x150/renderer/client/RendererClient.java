package me.x150.renderer.client;

import me.x150.renderer.event.Events;
import me.x150.renderer.renderer.EventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RendererClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Events.registerEventHandlerClass(new EventHandler());
    }
}
