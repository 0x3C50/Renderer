package me.x150.renderer.renderer.util;

import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.util.Identifier;

/**
 * A holder for shader effects
 */
class ShaderManager {
    /**
     * The glow shader
     */
    public static ManagedShaderEffect GLOW_SHADER = ShaderEffectManager.getInstance().manage(new Identifier("renderer", "shaders/post/glow.json"));
    /**
     * The blur mask shader
     */
    public static ManagedShaderEffect BLUR_MASK_SHADER = ShaderEffectManager.getInstance().manage(new Identifier("renderer", "shaders/post/blur_mask.json"));
}
