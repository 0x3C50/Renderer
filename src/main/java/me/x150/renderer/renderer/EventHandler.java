package me.x150.renderer.renderer;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class EventHandler {
    @EventListener(type= EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        if (event.getShift() == Shift.POST) {
            Renderer3d.renderFadingBlocks(event.getStack());
        }
    }

    // TESTING -
    @EventListener(type=EventType.HUD_RENDER)
    void a(RenderEvent ev) {
        if (ev.getShift() == Shift.POST) {
            MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES,() -> {
                Renderer2d.renderRoundedQuad(ev.getStack(),RendererUtils.modify(Color.RED,-1,-1,-1,100),5,5,100,100,20,20);
            });
        }
    }

    @EventListener(type=EventType.WORLD_RENDER)
    void b(RenderEvent we) {
        if (we.getShift() == Shift.POST) {
            Vec3d offset = new Vec3d(0.01,0.01,0.01);
            Renderer3d.renderFilled(we.getStack(), Vec3d.ZERO.add(offset),new Vec3d(1,1,1).subtract(offset.multiply(2)), Color.RED);
            Renderer3d.startRenderingThroughWalls();
            Renderer3d.renderLine(we.getStack(),Renderer3d.getCrosshairVector(),Vec3d.ZERO.add(.5,.5,.5),Color.BLUE);
            Renderer3d.renderBlockWithEdges(we.getStack(),new Vec3d(5,5,5),new Vec3d(1,1,1), Color.RED, Color.GREEN);
            Renderer3d.stopRenderingThroughWalls();
        }
    }
    // - TESTING
}
