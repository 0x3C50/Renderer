package me.x150.renderer.client;

import me.x150.MessageSubscription;
import me.x150.renderer.event.Events;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.Renderer3d;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Environment(EnvType.CLIENT)
public class RendererClient implements ClientModInitializer {
    public static Logger logger = LoggerFactory.getLogger("renderer");
    static ObjFile oj;
    FontRenderer fr;

    @Override
    public void onInitializeClient() {
        Events.manager.registerSubscribers(this); // Testing
    }

    //    @MessageSubscription
    //    void onHud(RenderEvent.Hud hud) {
    //        if (fr == null) {
    //            fr = new FontRenderer(new Font[] { new Font("Comfortaa", Font.PLAIN, 40), }, 9);
    //        }
    //        MinecraftClient client = MinecraftClient.getInstance();
    //        MSAAFramebuffer.use(16, () -> {
    //            for (Entity entity : client.world.getEntities()) {
    //                if (entity == client.player) {
    //                    continue;
    //                }
    //                float d = client.getTickDelta();
    //                Vec3d p = new Vec3d(MathHelper.lerp(d, entity.prevX, entity.getX()),
    //                    MathHelper.lerp(d, entity.prevY, entity.getY()),
    //                    MathHelper.lerp(d, entity.prevZ, entity.getZ()));
    //                Vec3d vec3d = RendererUtils.worldSpaceToScreenSpace(p.add(0, entity.getHeight() + 0.3, 0));
    //                if (RendererUtils.screenSpaceCoordinateIsVisible(vec3d)) {
    //                    String simpleName = entity.getClass().getSimpleName();
    //                    float width = fr.getStringWidth(simpleName);
    //                    float height = fr.getStringHeight(simpleName);
    //                    float pad = 5;
    //                    MatrixStack emptyMatrixStack = RendererUtils.getEmptyMatrixStack();
    //                    Renderer2d.renderRoundedQuad(emptyMatrixStack,
    //                        new Color(20, 20, 20, 100),
    //                        vec3d.x - width / 2d - pad,
    //                        vec3d.y - height - pad * 2,
    //                        vec3d.x + width / 2d + pad,
    //                        vec3d.y,
    //                        5,
    //                        5,
    //                        10,
    //                        10,
    //                        5);
    //                    Renderer2d.renderRoundedOutline(emptyMatrixStack,
    //                        Color.RED,
    //                        vec3d.x - width / 2d - pad,
    //                        vec3d.y - height - pad * 2,
    //                        vec3d.x + width / 2d + pad,
    //                        vec3d.y,
    //                        5,
    //                        5,
    //                        10,
    //                        10,
    //                        0.5f,
    //                        5);
    //                    fr.drawCenteredString(emptyMatrixStack, simpleName, (float) vec3d.x, (float) vec3d.y - pad - height, 1f, 1f, 1f, 1f);
    //                }
    //            }
    //        });
    //    }

    @MessageSubscription
    void onWorld(RenderEvent.World wor) {
        if (oj == null) {
            File f = new File("/home/x150/Downloads/thefucking.obj");
            try {
                oj = new ObjFile(new FileInputStream(f));
                oj.linkMaterialFile(new File("/home/x150/Downloads/thefucking.mtl"));
                oj.read();
                //                oj.flipYAxis();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        MatrixStack matrixStack = wor.getMatrixStack();
        matrixStack.push();
        //        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180f));
        Renderer3d.renderObjFile(matrixStack, oj, new Vec3d(100, 100, 100), 1, 1, 1);
        matrixStack.pop();
    }
}
