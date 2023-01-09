package me.x150.renderer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.event.Events;
import me.x150.renderer.event.RenderEvent;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.RenderProfiler;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    // camera bob render fix
    private boolean vb;
    private boolean dis;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void renderer_postWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        RenderProfiler.begin("World");
        if (vb) {
            MinecraftClient.getInstance().options.getBobView().setValue(true);
            vb = false;
        }
        RendererUtils.lastProjMat.set(RenderSystem.getProjectionMatrix());
        RendererUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
        RendererUtils.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
        Events.manager.send(new RenderEvent.World(matrix));
        Renderer3d.renderFadingBlocks(matrix);
        RenderProfiler.pop();
    }

    @Inject(at = @At("HEAD"), method = "renderWorld")
    private void renderer_preWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        dis = true;
    }

    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true)
    private void renderer_fixBobView(MatrixStack matrices, float f, CallbackInfo ci) {
        if (MinecraftClient.getInstance().options.getBobView().getValue() && dis) {
            vb = true;
            MinecraftClient.getInstance().options.getBobView().setValue(false);
            dis = false;
            ci.cancel();
        }
    }
}
