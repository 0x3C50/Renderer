package me.x150.testmod;

import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

public class Handler {

    FontRenderer fr;

    @MessageSubscription
    void hud(RenderEvent.Hud hud) {
        if (fr == null) {
            fr = new FontRenderer(new Font[] {
                new Font("Ubuntu", Font.PLAIN, 8)
            }, 9f);
        }
        String text = "Hello world 123 +- 100;:- %$ äöü # ^° µ€@«amongus»";
        float width = fr.getStringWidth(text);
        float height = fr.getStringHeight(text);
        MSAAFramebuffer.use(8, () -> {
            Renderer2d.renderRoundedQuad(hud.getMatrixStack(), new Color(20, 20, 20, 100), 5, 5, 5+width+5, 5+height+5, 5, 10);
            fr.drawString(hud.getMatrixStack(), text, 5+2.5f, 5+2.5f, 1f, 1f, 1f, 1f);
        });

        MinecraftClient instance = MinecraftClient.getInstance();
        Mouse m = instance.mouse;
        Window w = instance.getWindow();
        double x = m.getX() / w.getFramebufferWidth() * w.getScaledWidth();
        double y = m.getY() / w.getFramebufferHeight() * w.getScaledHeight();
        Vec3d close = RendererUtils.screenSpaceToWorldSpace(x, y, 0);
        Vec3d far = RendererUtils.screenSpaceToWorldSpace(x, y, 1);
        RaycastContext rc = new RaycastContext(close, far, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, instance.player);
        Vec3d pos = instance.world.raycast(rc).getPos();
        String format = String.format(Locale.ENGLISH, "%.2f %.2f %.2f", pos.x, pos.y, pos.z);
        fr.drawCenteredString(hud.getMatrixStack(), format, (float) x, (float) y-fr.getStringHeight(format)-2, 1f, 1f, 1f, 1f);
    }
}
