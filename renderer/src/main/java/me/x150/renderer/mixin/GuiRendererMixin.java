package me.x150.renderer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@ModifyConstant(method = "renderPreparedDraws", constant = @Constant(floatValue = 0.0f, ordinal = 2))
	float modifyLW(float constant) {
		// FIXME 23 Juni 2025 17:42: this shit
		return (Math.max(2.5F, (float) MinecraftClient.getInstance().getWindow().getFramebufferWidth() / 1920.0F * 2.5F));
	}
}
