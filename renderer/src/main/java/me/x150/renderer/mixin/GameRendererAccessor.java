package me.x150.renderer.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.util.Pool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	@Accessor
	Pool getPool();
	@Accessor
	FogRenderer getFogRenderer();
}
