package me.x150.renderer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.x150.renderer.fontng.FontScalingRegistry;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setScaleFactor(I)V"))
	void preSetScaleFactor(CallbackInfo ci, @Local(ordinal = 0) int i) {
		FontScalingRegistry.resize(i);
	}
}
