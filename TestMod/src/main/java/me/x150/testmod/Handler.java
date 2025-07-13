package me.x150.testmod;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.SneakyThrows;
import me.x150.renderer.fontng.FTLibrary;
import me.x150.renderer.fontng.Font;
import me.x150.renderer.fontng.FontScalingRegistry;
import me.x150.renderer.fontng.GlyphBuffer;
import me.x150.renderer.mixin.GameRendererAccessor;
import me.x150.renderer.render.CustomRenderLayers;
import me.x150.renderer.render.ExtendedDrawContext;
import me.x150.renderer.render.WorldRenderContext;
import me.x150.renderer.shader.Shaders;
import me.x150.renderer.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;

import java.io.InputStream;

public class Handler {
	private static boolean inited = false;

	private static Font font, emojiFont;
	private static GlyphBuffer gb;

	private static Framebuffer fb1;

	private static NativeImage ni;

	@SneakyThrows
	public static void hud(DrawContext context) {
		//		MatrixStack mat = context.getMatrices();
		//		mat.push();
		//		mat.translate(10, 10, 0);
		//		mat.scale(sc * 10, sc * 10, 1);


		if (ni == null) {
			Resource resource = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(Identifier.of("testmod", "untitled.png"));
			InputStream inputStream = resource.getInputStream();

			try {
				ni = NativeImage.read(inputStream);
			} catch (Throwable var8) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			inputStream.close();
		}

		FrameGraphBuilder fgb = new FrameGraphBuilder();
//		Framebuffer fb = MinecraftClient.getInstance().getFramebuffer();
		if (fb1 == null) fb1 = new SimpleFramebuffer("test", 1920, 1080, false);
		RenderSystem.getDevice().createCommandEncoder().writeToTexture(fb1.getColorAttachment(), ni, 0, 0, 0, 0, 1920, 1080, 0, 0);

		Shaders.drawBlur(fgb, 12, 6.6f, fb1);
		fgb.run(((GameRendererAccessor)MinecraftClient.getInstance().gameRenderer).getPool());

		//		mat.pop();
		//		context.draw();
				if (!inited) {
					inited = true;
					FTLibrary ftl = new FTLibrary();

					font = new Font(ftl, "Roboto-Regular.ttf", 0, 20);

					emojiFont = new Font(ftl, "mt.ttf", 0, 20);

					FontScalingRegistry.register(font, emojiFont);

					gb = new GlyphBuffer();
				}

				gb.clear();
				gb.addString(font, "search", 0, 0);

				gb.offsetToTopLeft();


//				context.fill(99, 99, 101, 101, 0xFFFFFFFF);

//				gb.drawDebuggingInformation(context, 100, 100);

				gb.draw(context, 100, 100);

//				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(1536));
//				MatrixStack emp = RendererUtils.getEmptyMatrixStack();
//				gb.draw(immediate, emp, 100, 100);
//				immediate.draw();
		RenderSystem.lineWidth(5f);
		ExtendedDrawContext.drawLine(context, 5, 5, 100, 100, 5, new Color(0xFFFFFF00));
		ExtendedDrawContext.drawRoundedRect(context, 10, 10, 200, 200, new Vector4f(5, 10, 15, 20), new Color(0xFFAABBCC));
		ExtendedDrawContext.drawEllipse(context, 100, 10, 200, 200, new Color(0xFFAABBCC));
//		me.x150.testmod.render.ExtendedDrawContext.drawTexturedRoundedRect(context, 100, 100, 50, 50, new Vector4f(15), new Color(1f, 1f, 0f, 1f), Identifier.of("testmod", "test.png"));
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
