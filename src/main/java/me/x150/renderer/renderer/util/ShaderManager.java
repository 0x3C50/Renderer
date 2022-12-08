package me.x150.renderer.renderer.util;

import me.x150.renderer.renderer.util.shader.Shader;

/**
 * A holder for shader effects
 */
class ShaderManager {
    /**
     * The glow shader
     */
    public static Shader GLOW_SHADER = Shader.create("glow", shader -> {
    });
    /**
     * The blur mask shader
     */
    public static Shader BLUR_MASK_SHADER = Shader.create("blur_mask", shader -> {
    });
}
