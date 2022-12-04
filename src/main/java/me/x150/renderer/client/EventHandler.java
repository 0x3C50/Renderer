package me.x150.renderer.client;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.Renderer3d;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.util.BlurMaskFramebuffer;
import me.x150.renderer.renderer.util.GlowFramebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

class EventHandler {
    @EventListener(shift = Shift.POST, value = EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        Renderer3d.renderFadingBlocks(event.getStack());
    }

//        @EventListener(EventType.WORLD_RENDER)
//        void test(RenderEvent ev) {
//            MatrixStack stack = ev.getStack();
//            GlowFramebuffer.use(() -> {
//                Renderer3d.stopRenderingThroughWalls();
//                Renderer3d.renderFilled(Vec3d.ZERO, new Vec3d(10, 10, 10), Color.RED).drawWithoutVBO(stack);
//            });
//            GlowFramebuffer.draw(32f);
//        }
}
