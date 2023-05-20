package me.x150.renderer.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvents {
    public static final Event<RenderEvent> WORLD = create();
    public static final Event<RenderEvent> HUD = create();

    private static Event<RenderEvent> create() {
        return EventFactory.createArrayBacked(RenderEvent.class, listeners -> matrixStack -> {
            for (RenderEvent listener : listeners) {
                listener.rendered(matrixStack);
            }
        });
    }

    @FunctionalInterface
    public interface RenderEvent {
        void rendered(MatrixStack matrixStack);
    }
}
