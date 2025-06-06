package me.x150.renderer.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PostEffectPass.class)
public interface PostEffectPassAccessor {
	@Accessor("uniformBuffers")
	Map<String, GpuBuffer> getUniformBuffers();
}
