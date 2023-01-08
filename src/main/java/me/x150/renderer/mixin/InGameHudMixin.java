package me.x150.renderer.mixin;

import me.x150.MessageManager;
import me.x150.renderer.event.Event;
import me.x150.renderer.event.Events;
import me.x150.renderer.event.RenderEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="render", at = @At("HEAD"), cancellable = true)
    void renderer_preHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        RenderEvent.Hud o = new RenderEvent.Hud(matrices, Event.Shift.PRE);
        Events.manager.send(o);
        if (o.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    void renderer_postHud(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Events.manager.send(new RenderEvent.Hud(matrices, Event.Shift.POST));
    }
}
