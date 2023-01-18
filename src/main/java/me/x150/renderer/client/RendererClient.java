package me.x150.renderer.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.MessageSubscription;
import me.x150.renderer.event.Events;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.render.SVGFile;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class RendererClient implements ClientModInitializer {
    public static Logger logger = LoggerFactory.getLogger("Renderer");
    SVGFile svf = new SVGFile("""
        <svg xmlns="http://www.w3.org/2000/svg" shape-rendering="geometricPrecision" text-rendering="geometricPrecision" image-rendering="optimizeQuality" fill-rule="evenodd" clip-rule="evenodd" viewBox="0 0 512 451.47"><path fill-rule="nonzero" d="m98.04 164.23 146.41-48.75V38.3l-18.51 18.72c-4.46 4.54-11.76 4.6-16.3.14-4.54-4.46-4.6-11.77-.14-16.31l36.98-37.4c2.59-2.63 6.12-3.76 9.53-3.38 3.4-.38 6.93.75 9.51 3.38l36.99 37.4c4.46 4.54 4.4 11.85-.14 16.31-4.54 4.46-11.84 4.4-16.3-.14l-18.5-18.71v77.44l148.25 50.76c5.05 1.71 8.15 6.58 7.82 11.67l.01.62v174.62l61.57 46.37-3.83-26.09c-.92-6.31 3.45-12.18 9.76-13.1 6.31-.92 12.17 3.45 13.09 9.76l7.64 52.04c.53 3.67-.72 7.2-3.11 9.68-1.74 2.9-4.74 5.01-8.35 5.49l-52.13 7.04c-6.3.85-12.11-3.58-12.95-9.89-.85-6.31 3.58-12.11 9.89-12.96l26.11-3.52-59.23-44.62-149.93 76.19a11.468 11.468 0 0 1-5.95 1.66c-2.32 0-4.47-.68-6.28-1.85l-148.26-77.35-61.03 45.97 26.11 3.52c6.31.85 10.74 6.65 9.9 12.96-.85 6.31-6.66 10.74-12.96 9.89l-52.13-7.04c-3.61-.48-6.6-2.59-8.35-5.49a11.546 11.546 0 0 1-3.11-9.68l7.64-52.04c.92-6.31 6.78-10.68 13.09-9.76 6.31.92 10.68 6.79 9.76 13.1l-3.83 26.09 61.54-46.35V175.65c0-5.76 4.22-10.54 9.72-11.42zm160.09-27.35c-.69.13-1.4.2-2.12.2-.9 0-1.79-.1-2.63-.3l-121.3 40.38 124.21 56.34 124.14-54.72-122.3-41.9zM131.2 300.35c0-3.32 2.69-6.01 6.01-6.01s6.01 2.69 6.01 6.01l-.03 25.13c.01.82.1 1.49.3 1.99l.31.43 15.01 6.98c3.01 1.38 4.32 4.95 2.94 7.96-1.39 3.01-4.96 4.32-7.97 2.94l-16.14-7.56c-2.48-1.54-4.18-3.64-5.23-6.2-.84-2.04-1.21-4.3-1.19-6.73l-.02-24.94zm113.46 120.54V253.57l-133.22-60.42v158.24l133.22 69.5zm155.87-225.73-132.74 58.5v167.43l132.74-67.45V195.16z"/></svg>
        """, 128, 128);

    @Override
    public void onInitializeClient() {
        Events.manager.registerSubscribers(this); // Testing
    }

    @MessageSubscription
    void onHud(RenderEvent.Hud hud) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        svf.render(hud.getMatrixStack(), 10, 10, 128, 128);
    }

    //    @MessageSubscription
    //    void onWorld(RenderEvent.World wor) {
    //        if (oj == null) {
    //            File f = new File("/home/x150/Downloads/thefucking.obj");
    //            try {
    //                oj = new ObjFile(new FileInputStream(f));
    //                oj.linkMaterialFile(new File("/home/x150/Downloads/thefucking.mtl"));
    //                oj.read();
    //                //                oj.flipYAxis();
    //            } catch (IOException e) {
    //                throw new RuntimeException(e);
    //            }
    //        }
    //        MatrixStack matrixStack = wor.getMatrixStack();
    //        matrixStack.push();
    //        //        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
    //        Renderer3d.renderObjFile(matrixStack, oj, new Vec3d(100, 100, 100), 1, 1, 1);
    //        matrixStack.pop();
    //    }
}
