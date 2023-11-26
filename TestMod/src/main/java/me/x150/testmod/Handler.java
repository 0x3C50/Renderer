package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler {
	static FontRenderer fr;
	private static ObjFile ob;

	@SneakyThrows
	public static void world(MatrixStack stack) {
		if (ob == null) {
			ob = new ObjFile("untitled.obj",
					ObjFile.ResourceProvider.ofPath(Path.of("/media/x150/stuff/Dev/Java/RenderLib2/run/")));
		}
		ob.draw(stack, new Matrix4f(), new Vec3d(0, 400, 0));
		OutlineFramebuffer.useAndDraw(() -> Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 300, 0), new Vec3d(5, 5, 5)), 1f, Color.GREEN, Color.BLACK);
	}
	static long l = 0;

	@SneakyThrows
	public static void hud(DrawContext matrices) {
		if (fr == null) {
			Font[] fonts = Font.createFonts(Objects.requireNonNull(Handler.class.getClassLoader().getResourceAsStream("real.otf")));
			fr = new FontRenderer(fonts, 64, 256, 2, "123");
		}
		if (l++ > 60 * 4) fr.drawString(matrices.getMatrices(), "012345689", 5, 5, 1, 1, 1, 1);
	}
}
