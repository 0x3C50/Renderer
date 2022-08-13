package me.x150.renderer.client;

import me.x150.renderer.event.EventListener;
import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.Renderer3d;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.util.CameraContext3D;
import me.x150.renderer.renderer.util.GlowFramebuffer;
import net.minecraft.util.math.Vec3d;

class EventHandler {

    @EventListener(shift = Shift.POST, type = EventType.WORLD_RENDER)
    void worldRendered(RenderEvent event) {
        Renderer3d.renderFadingBlocks(event.getStack());
    }
    // Testing the library - can be ignored

//    CameraContext3D build = CameraContext3D.builder().position(new Vec3d(-5, 0, 0)).fov(90f).build();
//    @EventListener(shift = Shift.POST, type = EventType.HUD_RENDER)
//    void hud(RenderEvent event) {
//        double v = Math.toRadians((System.currentTimeMillis() % 4000) / 4000d * 360d);
//        double v1 = Math.toRadians((System.currentTimeMillis() % 7000) / 7000d * 360d);
//        build.setPosition(new Vec3d(Math.sin(v) * 10, Math.sin(v1) * 5, Math.cos(v) * 10));
//        build.faceTowards(new Vec3d(.5, .5, .5));
//        build.use(() -> {
//            Renderer3d.renderOutline(Vec3d.ZERO, new Vec3d(1, 1, 1), Color.RED).drawWithoutVboWith3DContext(build);
//            Renderer3d.renderFilled(new Vec3d(2, 2, 2), new Vec3d(1, 1, 1), Color.BLUE).drawWithoutVboWith3DContext(build);
//            Renderer3d.renderLine(Vec3d.ZERO, new Vec3d(10, 0, 0), Color.YELLOW).drawWithoutVboWith3DContext(build);
//        }, 0, 0, 500, 500);
//    }
}
