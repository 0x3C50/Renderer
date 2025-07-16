package me.x150.renderer.mixin;

import net.fabricmc.fabric.impl.client.rendering.SpecialGuiElementRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SpecialGuiElementRegistryImpl.class)
public class SpecialGuiElementRegistryImplMixin {
	@Inject(method="onReady", at=@At("HEAD"), cancellable = true)
	private static void real(MinecraftClient client, VertexConsumerProvider.Immediate immediate, Map<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> specialElementRenderers, CallbackInfo ci) {
		if (specialElementRenderers.isEmpty()) ci.cancel();
	}

}
