package me.x150.testmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

public class TestMod implements ModInitializer {
	public static ManagedShaderEffect mse;
	/**
	 * Runs the mod initializer.
	 */
	@Override
	public void onInitialize() {
		mse = ShaderEffectManager.getInstance().manage(Identifier.of("testmod", "shaders/post/bruhhrubruh.json"));
//		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
//			MatrixStack matrixStack = drawContext.getMatrices();
//			matrixStack.push();
//
//			matrixStack.translate(40, 40, 0);
//			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((System.currentTimeMillis() % 5000) / 5000f * 360f));
//			matrixStack.translate(-40, -40, 0);
//
//			Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
//			Tessellator tessellator = Tessellator.getInstance();
//			BufferBuilder buffer = tessellator.getBuffer();
//
//			buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
//			buffer.vertex(positionMatrix, 20, 20, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
//			buffer.vertex(positionMatrix, 20, 60, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
//			buffer.vertex(positionMatrix, 60, 60, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
//			buffer.vertex(positionMatrix, 60, 20, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();
//
//			RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
//			RenderSystem.setShaderTexture(0, new Identifier("renderer", "icon.png"));
//			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//
//			tessellator.draw();
//			matrixStack.pop();
//		});
	}
}
