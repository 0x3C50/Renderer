package me.x150.renderer.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class RendererClient implements ClientModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("Renderer");

    @Override
    public void onInitializeClient() {
        //        Events.manager.registerSubscribers(this); // Testing
    }
}
