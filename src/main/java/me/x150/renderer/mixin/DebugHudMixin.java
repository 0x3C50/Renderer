package me.x150.renderer.mixin;

import me.x150.renderer.util.RenderProfiler;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getLeftText", at = @At("RETURN"))
    void addLeftText(CallbackInfoReturnable<List<String>> cir) {
        for (RenderProfiler.Entry allTickTime : RenderProfiler.getAllTickTimes()) {
            //            cir.getReturnValue().add("[Renderer bench] "+allTickTime.name()+": "+String.format("",allTickTime.end()- allTickTime.start())+"ns");
            cir.getReturnValue().add(String.format("[Renderer bench] %s: %07d ns", allTickTime.name(), allTickTime.end() - allTickTime.start()));
        }
    }
}
