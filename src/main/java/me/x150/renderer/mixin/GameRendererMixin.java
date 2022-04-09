package me.x150.renderer.mixin;

import me.x150.renderer.event.EventType;
import me.x150.renderer.event.Events;
import me.x150.renderer.event.Shift;
import me.x150.renderer.event.events.RenderEvent;
import me.x150.renderer.event.events.ScreenRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // camera bob render fix
    private boolean vb;
    private boolean dis;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void renderer_postWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (vb) {
            MinecraftClient.getInstance().options.bobView = true;
            vb = false;
        }
        Events.fireEvent(EventType.WORLD_RENDER, Shift.POST, new RenderEvent(matrix));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    void renderer_dispatchScreenRender(Screen instance, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!Events.fireEvent(EventType.SCREEN_RENDER, Shift.PRE, new ScreenRenderEvent(matrices, instance))) {
            instance.render(matrices, mouseX, mouseY, delta);
            Events.fireEvent(EventType.SCREEN_RENDER, Shift.POST, new ScreenRenderEvent(matrices, instance));
        }
    }

    @Inject(at = @At("HEAD"), method = "renderWorld", cancellable = true)
    private void renderer_preWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        if (Events.fireEvent(EventType.WORLD_RENDER, Shift.PRE, new RenderEvent(matrix))) {
            ci.cancel();
        }
        dis = true;
    }

    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void renderer_fixBobView(MatrixStack matrices, float f, CallbackInfo ci) {
        if (MinecraftClient.getInstance().options.bobView && dis) {
            vb = true;
            MinecraftClient.getInstance().options.bobView = false;
            dis = false;
            ci.cancel();
        }
    }
}
