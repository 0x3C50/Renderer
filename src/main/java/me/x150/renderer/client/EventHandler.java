package me.x150.renderer.client;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.Renderer3d;

class EventHandler {
    @EventListener(shift = Shift.POST, value = EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        Renderer3d.renderFadingBlocks(event.getStack());
    }

    //    @EventListener(EventType.HUD_RENDER)
    //    void test(RenderEvent ev) {
    //        MatrixStack stack = ev.getStack();
    //        BlurMaskFramebuffer.use(() -> {
    //            Renderer2d.renderRoundedQuad(stack, Color.WHITE, 50, 50, 100, 100, 5, 10);
    //        });
    //        BlurMaskFramebuffer.draw(8f);
    //    }
}
