package me.x150.renderer;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.Renderer3d;
import me.x150.renderer.renderer.color.Color;
import net.minecraft.util.math.Box;

public class EventHandler {
    @EventListener(type = EventType.WORLD_RENDER, shift = Shift.POST)
    public void onRender(RenderEvent event) {
        Renderer3d.renderOutline(new Box(0, 5, 0, 1, 6, 1), Color.WHITE).drawWithoutVBO(event.getStack());
    }
}
