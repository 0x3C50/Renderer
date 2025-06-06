package me.x150.renderer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import me.x150.renderer.util.MoreRenderLayer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RenderLayer.MultiPhase.class)
public abstract class RenderLayerMixin extends RenderLayer implements MoreRenderLayer {

	@Unique
	private Map<String, Object> uniforms = new HashMap<>();
	@Unique
	private Map<String, GpuTextureView> samplers = new HashMap<>();

	public RenderLayerMixin(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
		super(name, size, hasCrumbling, translucent, begin, end);
	}

	@Override
	public void setUniform(String u, Object v) {
		if (v == null) uniforms.remove(u);
		else uniforms.put(u, v);
	}

	@Override
	public void setSampler(String u, GpuTextureView v) {
		if (v == null) samplers.remove(u);
		else samplers.put(u, v);
	}

	@Inject(method = "draw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"))
	void beforeDraw(BuiltBuffer buffer, CallbackInfo ci, @Local(ordinal = 0) RenderPass pass) {
		uniforms.forEach((k, v) -> {
			int i = switch (v) {
				case float[] fa -> fa.length * 4;
				case int[] ia -> ia.length * 4;
				case Matrix4f mat -> 64;
				default -> throw new IllegalStateException("unknown uniform type " + v.getClass() + " (" + v + ")");
			};

			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				Std140Builder std140Builder = Std140Builder.onStack(memoryStack, i);

				switch (v) {
					case float[] fa -> {
						for (float v1 : fa) {
							std140Builder.putFloat(v1);
						}
					}
					case int[] ia -> {
						for (int i1 : ia) {
							std140Builder.putInt(i1);
						}
					}
					case Matrix4f mat -> {
						std140Builder.putMat4f(mat);
					}
					default -> throw new IllegalStateException("unknown uniform type " + v.getClass() + " (" + v + ")");
				}
				pass.setUniform(k, RenderSystem.getDevice().createBuffer(() -> getName() + " / " + k, 128, std140Builder.get()));
			}
		});
		samplers.forEach(pass::bindSampler);

	}
}
