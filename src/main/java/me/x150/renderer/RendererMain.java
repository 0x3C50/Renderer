package me.x150.renderer;
import me.x150.renderer.event.Events;
import net.fabricmc.api.ModInitializer;

public class RendererMain implements ModInitializer {
    @Override
    public void onInitialize() {
        Events.registerEventHandlerClass(new EventHandler());
    }
}

