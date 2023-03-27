package me.x150.testmod;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Handler {

    ObjFile objFile;
    FontRenderer fr;

    @MessageSubscription
    void onWorld(RenderEvent.World wr) throws IOException {
        if (objFile == null) {
            objFile = new ObjFile(new FileReader("untitled.obj"));
            objFile.linkMaterialFile(new File("untitled.mtl"));
            objFile.read();
        }
        MatrixStack matrixStack = wr.getMatrixStack();
        Matrix4f viewMat = new Matrix4f();
//        float yaw = MinecraftClient.getInstance().gameRenderer.getCamera().getYaw();
//        viewMat.rotateY((float) -Math.toRadians(yaw+180));
        Renderer3d.renderObjFile(matrixStack, viewMat, objFile, new Vec3d(37,71,4));
//        RenderSystem.setShaderTexture(0, 1);
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
