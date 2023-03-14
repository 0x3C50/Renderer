package me.x150.testmod;

import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.util.math.Vec3d;

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
        Renderer3d.renderObjFile(wr.getMatrixStack(), of, new Vec3d(0, 100, 0), 1f, 1f, 1f);
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
