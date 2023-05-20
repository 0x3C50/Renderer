package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.awt.Font;
import java.nio.file.Path;

public class Handler {

    static FontRenderer fr;
    private static ObjFile ob;

    @SneakyThrows
    public static void world(MatrixStack stack) {
        if (ob == null) {
            ob = new ObjFile("ULTRAKILL_VRig_menace1.obj", ObjFile.ResourceProvider.ofPath(Path.of("/home/x150/Downloads")));
        }
        ob.draw(stack, new Matrix4f(), new Vec3d(0, 400, 0));
    }

    public static void hud(MatrixStack stack) {
        if (fr == null) {
            fr = new FontRenderer(new Font[] {
                new Font("Ubuntu", Font.PLAIN, 8)
            }, 9f);
        }
        String text = "Newline test\nabc\n\nactually kinda sick";
        float width = fr.getStringWidth(text);
        float height = fr.getStringHeight(text);
        MSAAFramebuffer.use(8, () -> {
            Renderer2d.renderRoundedQuad(stack, new Color(20, 20, 20, 100), 5, 5, 5+width+5, 5+height+5, 5, 10);
            fr.drawString(stack, text, 5+2.5f, 5+2.5f, 1f, 1f, 1f, 1f);
        });
    }
}
