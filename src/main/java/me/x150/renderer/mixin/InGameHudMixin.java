package me.x150.renderer.mixin;

import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.util.RenderProfiler;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("RETURN"))
    void renderer_postHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        RenderProfiler.begin("Hud");
        RenderEvents.HUD.invoker().rendered(matrices);
        RenderProfiler.pop();
    }
}
