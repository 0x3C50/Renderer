package me.x150.renderer.client;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.Renderer3d;

class EventHandler {
    @EventListener(shift = Shift.POST, type = EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        Renderer3d.renderFadingBlocks(event.getStack());
    }
}
