package me.x150.renderer.mixin;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PostEffectPass.class)
public interface PostEffectPassAccessor {
	@Mutable
	@Accessor("input")
	void renderer_setInput(Framebuffer framebuffer);

	@Mutable
	@Accessor("output")
	void renderer_setOutput(Framebuffer framebuffer);
}
