package me.x150.renderer.client;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.ClipStack;
import me.x150.renderer.renderer.MSAAFramebuffer;
import me.x150.renderer.renderer.Rectangle;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.Renderer3d;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.util.GlowFramebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

class EventHandler {
    @EventListener(shift = Shift.POST, value = EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        Renderer3d.renderFadingBlocks(event.getStack());
    }

//    @EventListener(EventType.WORLD_RENDER)
//    void test(RenderEvent ev) {
//        MatrixStack stack = ev.getStack();
//        Renderer3d.stopRenderingThroughWalls();
//        GlowFramebuffer.useAndDraw(() -> {
//            Renderer3d.renderFilled(Vec3d.ZERO, new Vec3d(1, 1, 1), Color.RED).drawWithoutVBO(stack);
//        }, 32f);
//    }
//
//    @EventListener(EventType.HUD_RENDER)
//    void testHud(RenderEvent ev) {
//        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
//            ClipStack.globalInstance.addWindow(ev.getStack(), new Rectangle(10, 10, 100, 100));
//            Renderer2d.renderRoundedQuad(ev.getStack(), Color.WHITE, 5, 5, 100, 100, 10, 10);
//            ClipStack.globalInstance.popWindow();
//        });
//    }
}
