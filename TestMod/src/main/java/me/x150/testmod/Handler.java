package me.x150.testmod;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.SneakyThrows;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.render.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30C;

import java.awt.*;

public class Handler {
	private static final SVGFile sv = new SVGFile("""
			<?xml version="1.0" encoding="UTF-8"?>
			<!-- Generator: Adobe Illustrator 26.0.1, SVG Export Plug-In . SVG Version: 6.00 Build 0)  -->
			<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" version="1.1" id="Capa_1" x="0px" y="0px" viewBox="0 0 24 24" style="enable-background:new 0 0 24 24;" xml:space="preserve" width="512" height="512">
			<g>
				<path d="M5.566,14.276c0-0.664,2.521-1.037,3.694-1.128l0.112,0.065c-0.451,0.082-2.26,0.401-2.26,0.817   c0,0.453,2.775,0.75,4.385,0.75c2.735,0,4.595-0.414,5.095-0.55l0.7,0.407c-0.479,0.235-2.536,0.849-5.795,0.849   C7.876,15.486,5.566,14.778,5.566,14.276L5.566,14.276z M11.085,23.604c-1.433,0.012-3.183-0.106-4.655-0.353l-0.137,0.078   c1.467,0.43,3.509,0.69,5.752,0.67c4.406-0.038,7.978-1.131,8.048-2.446l-0.051-0.03C19.747,21.884,17.842,23.545,11.085,23.604   L11.085,23.604z M11.46,22.757c3.606-0.032,7.641-0.737,7.63-1.923c-0.002-0.215-0.142-0.363-0.264-0.451l-0.059,0.034   c-0.333,0.919-3.15,1.598-7.313,1.634c-2.686,0.024-6.406-0.62-6.413-1.363c-0.006-0.746,1.763-1.155,1.763-1.155L6.679,19.46   c-1.185,0.163-3.37,0.731-3.363,1.551C3.326,22.196,8.346,22.784,11.46,22.757L11.46,22.757z M19.584,14.593   c-0.073,1.391-1.358,2.258-2.643,2.989l0.116,0.067c1.371-0.386,3.817-1.509,3.615-3.235c-0.101-0.86-0.888-1.476-1.913-1.476   c-0.32,0-0.604,0.056-0.834,0.126l-0.001,0.002l-0.049,0.122C18.793,13.011,19.631,13.681,19.584,14.593L19.584,14.593z    M8.991,18.727c-0.418,0.083-1.33,0.293-1.33,0.736c0,0.614,1.951,1.085,3.835,1.085c2.592,0,3.654-0.667,3.702-0.702l-1.078-0.624   c-0.458,0.109-1.231,0.28-2.621,0.28c-1.552,0-2.563-0.265-2.563-0.556c0-0.062,0.039-0.135,0.11-0.189L8.991,18.727z    M15.359,16.656C14.763,16.825,13.42,17.1,11.5,17.1c-1.884,0-3.424-0.322-3.429-0.702c-0.004-0.253,0.302-0.363,0.302-0.363   l-0.054-0.031c-0.903,0.159-1.741,0.406-1.736,0.775c0.008,0.67,2.57,1.172,4.913,1.172c1.992,0,3.905-0.334,4.768-0.772   L15.359,16.656z"/>
				<path d="M14.845,1.717c0,3.693-5.06,5.106-5.06,7.731c0,1.843,1.222,2.999,1.899,3.73l-0.055,0.032   c-0.854-0.534-3.1-1.876-3.1-4.093c0-3.112,5.813-4.599,5.813-8.134c0-0.435-0.064-0.769-0.11-0.948L14.29,0   C14.474,0.231,14.845,0.81,14.845,1.717L14.845,1.717z M16.509,4.987l-0.061-0.035c-1.101,0.369-4.491,1.707-4.491,4.202   c0,1.411,1.378,2.193,1.378,3.516c0,0.472-0.267,0.915-0.483,1.179l0.109,0.063c0.574-0.373,1.589-1.18,1.589-2.222   c0-0.883-1.221-1.943-1.221-3.078C13.328,6.825,15.685,5.423,16.509,4.987L16.509,4.987z"/>
			</g>
			</svg>
			""", 128, 128);
	static FontRenderer fr;

	static Framebuffer fb;
	//	private static ObjFile ob;

	@SneakyThrows
	public static void world(MatrixStack stack) {
//		if (ob == null) {
//			ob = new ObjFile("untitled.obj",
//					ObjFile.ResourceProvider.ofPath(Path.of("/home/x150")));
//		}
//		ob.draw(stack, new Matrix4f(), new Vec3d(0, 200, 0));
//		OutlineFramebuffer.useAndDraw(() -> Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 300, 0), new Vec3d(5, 5, 5)), 1f, Color.GREEN, Color.BLACK);
//
//		Renderer3d.renderFilled(stack, Color.RED, new Vec3d(0, 200, 0), new Vec3d(1, 1, 1));
//		Renderer3d.renderFilled(stack, Color.GREEN, new Vec3d(2, 202, 2), new Vec3d(1, 1, 1));
//
////		stack.push();
////		stack.translate(System.currentTimeMillis() % 5000 / 5000d * 100, 0, 0);
//
		RenderSystem.enableDepthTest();
		LaggingMaskFramebuffer.use(() -> {
			Renderer3d.stopRenderThroughWalls();
			Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 70, 0), new Vec3d(2, 2, 2));
			Renderer3d.renderThroughWalls();
		});
		LaggingMaskFramebuffer.draw();
	}

	@SneakyThrows
	public static void hud(DrawContext context) {
		if (fr == null) {
			Font fn = Font.decode("FreeSerif");
			fr = new FontRenderer(new Font[]{fn}, 64, 5, 2, "123")
					.roundCoordinates(true);
		}
//		fr.drawString(matrices.getMatrices(), "012345689", 5f + ((System.currentTimeMillis() % 5000) / 5000f) * 200f, 5f
//				+ ((System.currentTimeMillis() % 7000) / 7000f) * 100f, 1, 1, 1, 1);
//		sv.render(matrices.getMatrices(), 5, 5, 128, 128);
		LaggingMaskFramebuffer obtain = LaggingMaskFramebuffer.obtain();
		int depthAttachment = obtain.getDepthAttachment();
//		System.out.println(depthAttachment);
		RenderSystem.setShaderTexture(0, depthAttachment);
		Renderer2d.renderTexture(context.getMatrices(), 0, 0, 100, 100);


		MinecraftClient mc = MinecraftClient.getInstance();
		Framebuffer frame = mc.getFramebuffer();

		if (fb == null) {
			fb = new SimpleFramebuffer(frame.textureWidth, frame.textureHeight, false, false);
		}
		if (fb.textureWidth != frame.textureWidth || fb.textureHeight != frame.textureHeight) {
			fb.resize(frame.textureWidth, frame.textureHeight, false);
		}

		fb.clear(false);

		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, fb.fbo);
		fb.beginWrite(false); // false = don't set the viewport

		var entity = mc.player;

		var matrices = context.getMatrices();
		matrices.push();

		matrices.translate(50f, 50f, 100);
		matrices.scale(75 * 1 * 50f / 64f, -75 * 1f * 50f / 64f, 75 * 1f);

		matrices.translate(0, entity.getHeight() / -2f, 0);

		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45));

		RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
		EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
		dispatcher.setRenderShadows(false);
		VertexConsumerProvider.Immediate eb = mc.getBufferBuilders().getEntityVertexConsumers();
		dispatcher.render(entity, 0, 0, 0, 0, 0, matrices, eb, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		dispatcher.setRenderShadows(true);
		eb.draw();

		matrices.pop();

		fb.endWrite();

		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, frame.fbo);
		frame.beginWrite(false);

		double scale = mc.getWindow().getScaleFactor();

		RenderSystem.setShaderTexture(0, fb.getColorAttachment());
		Renderer2d.renderTexture(matrices, 100, 0, 100, 100, 0, fb.textureHeight, 100 * scale, -100 * scale, fb.textureWidth, fb.textureHeight);


//		MatrixStack emptyMatrixStack = RendererUtils.getEmptyMatrixStack();



//		String collect = Arrays.stream(RenderProfiler.getAllTickTimes()).map(it -> String.format("%s: %07d ns", it.name(), it.end() - it.start())).collect(Collectors.joining("\n"));
//
//		MatrixStack emptyMatrixStack = RendererUtils.getEmptyMatrixStack();
//		emptyMatrixStack.push();
//		emptyMatrixStack.scale(.5f, .5f, 1);
//		fr.drawString(emptyMatrixStack, collect, 5, 5, 1f, 1f, 1f, 1f);
//		emptyMatrixStack.pop();

//		ShaderManager.GAUSSIAN_BLUR.setUniformValue("width", 16);
//		ShaderManager.GAUSSIAN_BLUR.setUniformValue("sigma", 4f);
//		ShaderManager.GAUSSIAN_BLUR.render(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));
	}
}
