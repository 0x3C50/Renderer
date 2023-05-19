package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;
import java.util.Objects;

public class Handler {

    FontRenderer fr;

    @MessageSubscription
    void hud(RenderEvent.Hud hud) {
        if (fr == null) {
            fr = new FontRenderer(new Font[] {
                new Font("Ubuntu", Font.PLAIN, 8)
            }, 9f);
        }
        String text = "Newline test\nabcdefg\n\nactually kinda sick";
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
        RaycastContext rc = new RaycastContext(close, far, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, Objects.requireNonNull(instance.player));
        Vec3d pos = Objects.requireNonNull(instance.world).raycast(rc).getPos();
        String format = String.format(Locale.ENGLISH, "%.2f %.2f %.2f", pos.x, pos.y, pos.z);
        fr.drawCenteredString(hud.getMatrixStack(), format, (float) x, (float) y-fr.getStringHeight(format)-2, 1f, 1f, 1f, 1f);
    }

    ObjFile of;

    @MessageSubscription
    @SneakyThrows
    void world(RenderEvent.World world) {
        // this one might not work for everyone, but hey this is a mod for testing the library so who cares :^)
        if (of == null) {
            of = new ObjFile(new FileReader("/media/x150/stuff/Dev/Java/RenderLib2/run/untitled.obj"));
            of.linkMaterialFile(new File("/media/x150/stuff/Dev/Java/RenderLib2/run/untitled.mtl"));
            of.read();
        }
        OutlineFramebuffer.useAndDraw(() -> {
            Renderer3d.renderObjFile(world.getMatrixStack(), new Matrix4f(), of, new Vec3d(0, 100, 0));
        }, 1, Color.RED, new Color(0, 0, 0, 0));
    }
}
