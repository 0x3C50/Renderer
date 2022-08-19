package me.x150.renderer.renderer.util;

import net.minecraft.client.gl.Framebuffer;

public interface ShaderEffectDuck {
    void addFakeTarget(String name, Framebuffer buffer);
}
