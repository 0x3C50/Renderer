package me.x150.renderer.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.FastMStack;
import me.x150.renderer.util.RenderProfiler;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"))
	void renderer_postWorldRender(WorldRenderer instance, ObjectAllocator objectAllocator, RenderTickCounter renderTickCounter, boolean b, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, Operation<Void> original) {
		original.call(instance, objectAllocator, renderTickCounter, b, camera, gameRenderer, lightmapTextureManager, matrix4f, matrix4f2);

		MatrixStack matrix = new FastMStack();
		matrix.multiplyPositionMatrix(matrix4f);

		RenderProfiler.begin("World");

		RendererUtils.lastProjMat.set(RenderSystem.getProjectionMatrix());
		RendererUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
		RendererUtils.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, RendererUtils.lastViewport);

		RenderEvents.WORLD.invoker().rendered(matrix);
		Renderer3d.renderFadingBlocks(matrix);

		RenderProfiler.pop();
	}
}
