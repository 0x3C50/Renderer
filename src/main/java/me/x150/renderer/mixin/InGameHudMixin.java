package me.x150.renderer.mixin;

import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.util.RenderProfiler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(method = "render", at = @At("RETURN"))
	void renderer_postHud(DrawContext context, float tickDelta, CallbackInfo ci) {
		RenderProfiler.begin("Hud");
		RenderEvents.HUD.invoker().rendered(context);
		RenderProfiler.pop();
	}
}
