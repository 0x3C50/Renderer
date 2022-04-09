package me.x150.renderer.mixin.hud;

import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Events;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.renderer.Renderer3d;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void renderer_preHudRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (Events.fireEvent(EventType.HUD_RENDER, Shift.PRE, new RenderEvent(Renderer3d.getEmptyMatrixStack()))) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    void renderer_postHudRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Events.fireEvent(EventType.HUD_RENDER, Shift.POST, new RenderEvent(Renderer3d.getEmptyMatrixStack()));
    }
}
