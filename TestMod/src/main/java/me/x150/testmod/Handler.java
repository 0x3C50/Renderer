package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.fontng.Font;
import me.x150.renderer.fontng.GlyphBuffer;
import me.x150.renderer.render.CustomRenderLayers;
import me.x150.renderer.render.ExtendedDrawContext;
import me.x150.renderer.render.WorldRenderContext;
import me.x150.renderer.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

public class Handler {
	private static boolean inited = false;

	private static Font font, emojiFont;
	private static GlyphBuffer gb;

	@SneakyThrows
	public static void hud(DrawContext context) {
		//		MatrixStack mat = context.getMatrices();
		//		mat.push();
		//		mat.translate(10, 10, 0);
		//		mat.scale(sc * 10, sc * 10, 1);
		ExtendedDrawContext.drawRR(context, 10, 10, 200, 200, new Vector4f(5, 10, 15, 20), new Color(0xFFAABBCC));
		ExtendedDrawContext.drawLine(context, 5, 5, 100, 100, 5, new Color(0xFFFFFF00));
		//		mat.pop();
		//		context.draw();
		//		if (!inited) {
		//			inited = true;
		//			FTLibrary ftl = new FTLibrary();
		//
		//			font = new Font(ftl, "Roboto-Regular.ttf", 0, 20);
		//
		//			emojiFont = new Font(ftl, "mt.ttf", 0, 20);
		//
		//			FontScalingRegistry.register(font, emojiFont);
		//
		//			gb = new GlyphBuffer();
		//		}
		//
		//		gb.clear();
		//		gb.addString(emojiFont, "search", 0, 0)
		//				.then(font, "search for some shit", 5, -3);
		//
		//		gb.offsetToTopLeft();
		//
		//
		//		context.fill(99, 99, 101, 101, 0xFFFFFFFF);
		//
		//		gb.drawDebuggingInformation(context, 100, 100);
		//
		//		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(1536));
		//		MatrixStack emp = RendererUtils.getEmptyMatrixStack();
		//		gb.draw(immediate, emp, 100, 100);
		//		immediate.draw();
	}

	public static void world(MatrixStack worldRenderContext) {
		MatrixStack stack = worldRenderContext;
		VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(new BufferAllocator(1024));
		WorldRenderContext rc = new WorldRenderContext(MinecraftClient.getInstance(), vcp);
		rc.drawFilledCube(stack, CustomRenderLayers.POS_COL_QUADS_WITH_DEPTH_TEST, new Vec3d(100, 100, 100), 1, 1, 1, new Color(0xAAFFFFFF));
		rc.drawFilledCube(stack, CustomRenderLayers.POS_COL_QUADS_NO_DEPTH_TEST, new Vec3d(105, 100, 105), 1, 1, 1, new Color(0xAAFF0000));
		rc.drawFilledCube(stack, RenderLayer.getDebugLineStrip(1), new Vec3d(110, 100, 105), 1, 1, 1, new Color(0xAAFF0000));
		rc.drawLine(stack, CustomRenderLayers.LINES_NO_DEPTH_TEST.apply(5d), new Vec3d(100, 100, 100), new Vec3d(105, 100, 105), new Color(0xFF0000FF));
		rc.drawLine(stack, CustomRenderLayers.LINES_NO_DEPTH_TEST.apply(0d), new Vec3d(100, 101, 100), new Vec3d(105, 101, 105), new Color(0xFF0000FF));
		vcp.draw();
	}
}
