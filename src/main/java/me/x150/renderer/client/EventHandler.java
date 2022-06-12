package me.x150.renderer.client;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.RenderAction;
import me.x150.renderer.renderer.Renderer3d;
import me.x150.renderer.renderer.color.Color;
import net.minecraft.util.math.Vec3d;

class EventHandler {
    @EventListener(shift = Shift.POST, type = EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        Renderer3d.renderFadingBlocks(event.getStack());

        Renderer3d.startRenderingThroughWalls();
        RenderAction ra = Renderer3d.renderLine(Vec3d.ZERO,new Vec3d(5,5,5), Color.WHITE);
        ra.drawWithoutVBO(event.getStack());
//        ra.delete();
    }
}
