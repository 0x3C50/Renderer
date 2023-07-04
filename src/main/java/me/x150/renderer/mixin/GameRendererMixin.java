package me.x150.renderer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.RenderProfiler;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@SuppressWarnings("SpellCheckingInspection")
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
	void renderer_postWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
		RenderProfiler.begin("World");

		RendererUtils.lastProjMat.set(RenderSystem.getProjectionMatrix());
		RendererUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
		RendererUtils.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
		RenderEvents.WORLD.invoker().rendered(matrix);
		Renderer3d.renderFadingBlocks(matrix);

		RenderProfiler.pop();
	}
}
