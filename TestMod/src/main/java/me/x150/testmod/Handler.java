package me.x150.testmod;

import me.x150.MessageSubscription;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Handler {

    ObjFile of;

    @MessageSubscription
    void real(RenderEvent.World wr) throws IOException {
        if (of == null) {
            of = new ObjFile(new FileReader("untitled.obj"));
            of.linkMaterialFile(new File("untitled.mtl"));
            of.read();
        }
        Renderer3d.renderObjFile(wr.getMatrixStack(), of, new Vec3d(0, 100, 0), 1f, 1f, 1f);
    }
}
