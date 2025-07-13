package me.x150.renderer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.util.RenderUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"))
	void renderer_postWorldRender(WorldRenderer instance, ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, Operation<Void> original) {
		original.call(instance, allocator, tickCounter, renderBlockOutline, camera, positionMatrix, projectionMatrix, fog, fogColor, shouldRenderSky);

		Profiler prof = Profilers.get();
		prof.swap("rendererLibWorld");

		MatrixStack matrix = new MatrixStack();
		matrix.multiplyPositionMatrix(positionMatrix);

		RenderUtils.lastProjMat.set(projectionMatrix);
		RenderUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
		RenderUtils.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, RenderUtils.lastViewport);

		RenderEvents.AFTER_WORLD.invoker().rendered(matrix);

		// restore state like the original world rendering code did
		GlStateManager._depthMask(true);
		GlStateManager._disableBlend();
	}
}
