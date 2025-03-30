package me.x150.renderer.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BufferBuilder.class)
public interface BufferBuilderAccessor {
	@Accessor("vertexPointer")
	long getMeTheFuckingPointerOfThisBitch();

	@Invoker("beginVertex")
	long beginNewVertex();

	@Accessor("vertexFormat")
	VertexFormat getVertexFormat();
}
