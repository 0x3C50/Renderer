package me.x150.renderer.client;

import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.util.RendererUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.awt.Font;

@Environment(EnvType.CLIENT)
public class RendererClient implements ClientModInitializer {
    FontRenderer fr;

    @Override
    public void onInitializeClient() {
        //        Events.manager.registerSubscribers(this); // Testing
    }

    @MessageSubscription
    void onHud(RenderEvent.Hud hud) {
        if (fr == null) {
            fr = new FontRenderer(new Font[] { new Font("JetBrains Mono", Font.PLAIN, 40), new Font("Comfortaa", Font.PLAIN, 40), }, 9);
        }
        MinecraftClient client = MinecraftClient.getInstance();
        MSAAFramebuffer.use(16, () -> {
            for (Entity entity : client.world.getEntities()) {
                if (entity == client.player) {
                    continue;
                }
                float d = client.getTickDelta();
                Vec3d p = new Vec3d(MathHelper.lerp(d, entity.prevX, entity.getX()),
                    MathHelper.lerp(d, entity.prevY, entity.getY()),
                    MathHelper.lerp(d, entity.prevZ, entity.getZ()));
                Vec3d vec3d = RendererUtils.worldSpaceToScreenSpace(p.add(0, entity.getHeight() + 0.3, 0));
                if (RendererUtils.screenSpaceCoordinateIsVisible(vec3d)) {
                    String simpleName = entity.getClass().getSimpleName();
                    float width = fr.getStringWidth(simpleName);
                    float height = fr.getStringHeight(simpleName);
                    float pad = 5;
                    Renderer2d.renderRoundedQuad(RendererUtils.getEmptyMatrixStack(),
                        new Color(20, 20, 20, 100),
                        vec3d.x - width / 2d - pad,
                        vec3d.y - height - pad * 2,
                        vec3d.x + width / 2d + pad,
                        vec3d.y,
                        5,
                        10);
                    fr.drawCenteredString(RendererUtils.getEmptyMatrixStack(), simpleName, (float) vec3d.x, (float) vec3d.y - pad - height, 1f, 1f, 1f, 1f);
                }
            }
        });
    }

    //    @MessageSubscription
    //    void onWorld(RenderEvent.World wor) {
    //    }
}
