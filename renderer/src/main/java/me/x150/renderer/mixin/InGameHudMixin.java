package me.x150.renderer.mixin;

import me.x150.renderer.event.RenderEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(method = "render", at = @At("RETURN"))
	void renderer_postHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
//		RenderProfiler.begin("Hud");
		Profiler prof = Profilers.get();
		prof.push("rendererLibHud");
		RenderEvents.HUD.invoker().rendered(context);
		prof.pop();
//		RenderProfiler.pop();
	}
}
