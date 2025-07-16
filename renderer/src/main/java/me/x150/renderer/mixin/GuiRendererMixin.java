package me.x150.renderer.mixin;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.GuiRenderer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

@Debug(export = true)
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@ModifyConstant(method = "renderPreparedDraws", constant = @Constant(floatValue = 0.0f, ordinal = 2))
	float modifyLW(float constant) {
		// FIXME 23 Juni 2025 17:42: this shit
		return (Math.max(2.5F, (float) MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
	}

	@Redirect(method="render(Ljava/util/function/Supplier;Lnet/minecraft/client/gl/Framebuffer;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;II)V",
	at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalInt;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/blaze3d/systems/RenderPass;"))
	RenderPass createPass(CommandEncoder instance, Supplier<String> stringSupplier, GpuTextureView colTex, OptionalInt colC, @Nullable GpuTextureView depTex, OptionalDouble depC) {
		return instance.createRenderPass(stringSupplier,
				Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, colTex), colC,
				Objects.requireNonNullElse(RenderSystem.outputDepthTextureOverride, depTex), depC);
	}
}
