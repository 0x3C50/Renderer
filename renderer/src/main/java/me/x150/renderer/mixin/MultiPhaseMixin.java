package me.x150.renderer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import me.x150.renderer.util.MoreMultiPhase;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(RenderLayer.MultiPhase.class)
public class MultiPhaseMixin implements MoreMultiPhase {
	@Unique
	private Consumer<RenderPass> renderPassSetup;
	@Override
	public RenderLayer.MultiPhase withRenderPassSetup(Consumer<RenderPass> rp) {
		this.renderPassSetup = rp;
		return ((RenderLayer.MultiPhase) (Object) this);
	}

	@Inject(method="draw",at= @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"))
	void preDraw(BuiltBuffer buffer, CallbackInfo ci, @Local RenderPass thePass) {
		if (renderPassSetup != null) renderPassSetup.accept(thePass);
	}
}
