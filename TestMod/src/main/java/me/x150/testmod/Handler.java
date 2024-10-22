package me.x150.testmod;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.SneakyThrows;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.function.UnaryOperator;

public class Handler {
	static FontRenderer fr;

//	static Framebuffer fb;
	//	private static ObjFile ob;

//	@SneakyThrows
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
//		RenderSystem.enableDepthTest();
//		LaggingMaskFramebuffer.use(() -> {
//			Renderer3d.stopRenderThroughWalls();
//			Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 70, 0), new Vec3d(2, 2, 2));
//			Renderer3d.renderThroughWalls();
//		});
//		LaggingMaskFramebuffer.draw();
	}

	@SneakyThrows
	public static void hud(DrawContext context) {
		if (fr == null) {
			Font fn = Font.decode("Roboto");
			fr = new FontRenderer(fn, 10);
			fr.roundCoordinates(true);
		}
		UnaryOperator<Style> theMod = style -> style.withUnderline(true).withStrikethrough(true).withColor(0xFFAA55);
		MutableText theText = Text.literal("The quick brown fox jumps over the lazy dog\n")
				.append(Text.literal("italic\n").styled(it -> it.withItalic(true)))
				.append(Text.literal("bold\n").styled(it -> it.withBold(true)))
				.append(Text.literal("bold italic\n").styled(it -> it.withBold(true).withItalic(true)))
				.append(Text.literal("under\n").styled(it -> it.withUnderline(true)))
				.append(Text.literal("strikethrough\n").styled(it -> it.withStrikethrough(true)))
				.append(Text.literal("Special chars: 1234@æđðħſ.ĸ|aa{a}()"));
		float x = 5f;
		float y = 5f;
		float width = fr.getTextWidth(theText);
		float height = fr.getTextHeight(theText);
		Renderer2d.renderQuad(context.getMatrices(), Color.RED, x, y, x+width, y+height);
		fr.drawText(context.getMatrices(), theText, x, y, 1);
		RenderSystem.disableBlend();
//		sv.render(matrices.getMatrices(), 5, 5, 128, 128);
//		LaggingMaskFramebuffer obtain = LaggingMaskFramebuffer.obtain();
//		int depthAttachment = obtain.getDepthAttachment();
////		System.out.println(depthAttachment);
//		RenderSystem.setShaderTexture(0, depthAttachment);
//		Renderer2d.renderTexture(context.getMatrices(), 0, 0, 100, 100);
//
//
//		MinecraftClient mc = MinecraftClient.getInstance();
//		Framebuffer frame = mc.getFramebuffer();
//
//		if (fb == null) {
//			fb = new SimpleFramebuffer(frame.textureWidth, frame.textureHeight, false, false);
//		}
//		if (fb.textureWidth != frame.textureWidth || fb.textureHeight != frame.textureHeight) {
//			fb.resize(frame.textureWidth, frame.textureHeight, false);
//		}
//
//		fb.clear(false);
//
//		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, fb.fbo);
//		fb.beginWrite(false); // false = don't set the viewport
//
//		var entity = mc.player;
//
//		var matrices = context.getMatrices();
//		matrices.push();
//
//		matrices.translate(50f, 50f, 100);
//		matrices.scale(75 * 1 * 50f / 64f, -75 * 1f * 50f / 64f, 75 * 1f);
//
//		matrices.translate(0, entity.getHeight() / -2f, 0);
//
//		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(35));
//		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45));
//
//		RenderSystem.setShaderLights(new Vector3f(.15f, 1, 0), new Vector3f(.15f, -1, 0));
//		EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
//		dispatcher.setRenderShadows(false);
//		VertexConsumerProvider.Immediate eb = mc.getBufferBuilders().getEntityVertexConsumers();
//		dispatcher.render(entity, 0, 0, 0, 0, 0, matrices, eb, LightmapTextureManager.MAX_LIGHT_COORDINATE);
//		dispatcher.setRenderShadows(true);
//		eb.draw();
//
//		matrices.pop();
//
//		fb.endWrite();
//
//		GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, frame.fbo);
//		frame.beginWrite(false);
//
//		double scale = mc.getWindow().getScaleFactor();
//
//		RenderSystem.setShaderTexture(0, fb.getColorAttachment());
//		Renderer2d.renderTexture(matrices, 100, 0, 100, 100, 0, fb.textureHeight, 100 * scale, -100 * scale, fb.textureWidth, fb.textureHeight);


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
