package me.x150.testmod;

import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Handler {

    ObjFile of;
    FontRenderer fr;

    @MessageSubscription
    void real(RenderEvent.World wr) throws IOException {
        if (of == null) {
            of = new ObjFile(new FileReader("untitled.obj"));
            of.linkMaterialFile(new File("untitled.mtl"));
            of.read();
        }
        MatrixStack matrixStack = wr.getMatrixStack();
//        matrixStack.push();
        Matrix4f viewMat = new Matrix4f();
        float d = (System.currentTimeMillis() % 2000) / 2000f * 360f;
        viewMat.rotateY((float) Math.toRadians(d));
        Renderer3d.renderObjFile(matrixStack, viewMat, of, new Vec3d(0, 100, 0));
//        matrixStack.pop();
    }

    @MessageSubscription
    void hud(RenderEvent.Hud hud) {
        if (fr == null) {
            fr = new FontRenderer(new Font[] {
                new Font("Ubuntu", Font.PLAIN, 8)
            }, 18f);
        }
        String text = "Hello world 123 +- 100;:- %$ äöü # ^° µ€@«amongus»";
        float width = fr.getStringWidth(text);
        float height = fr.getStringHeight(text);
        MSAAFramebuffer.use(8, () -> {
            Renderer2d.renderRoundedQuad(hud.getMatrixStack(), new Color(20, 20, 20, 100), 5, 5, 5+width+5, 5+height+5, 5, 10);
            fr.drawString(hud.getMatrixStack(), text, 5+2.5f, 5+2.5f, 1f, 1f, 1f, 1f);
        });
    }
}
