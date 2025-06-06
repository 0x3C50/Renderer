package me.x150.renderer.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@ModifyConstant(method = "renderPreparedDraws", constant = @Constant(floatValue = 0.0f, ordinal = 2))
	float modifyLW(float constant) {
		return RenderSystem.getShaderLineWidth();
	}
}
