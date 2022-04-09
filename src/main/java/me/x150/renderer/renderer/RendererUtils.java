package me.x150.renderer.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * Utils for rendering in minecraft
 */
public class RendererUtils {
    /**
     * Sets up rendering and resets everything that should be reset
     */
    public static void setupRender() {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    /**
     * Linear interpolation between two ints
     * @param from Range from
     * @param to Range to
     * @param delta Range delta
     * @return The interpolated value between from and to
     */
    public static int lerp(int from, int to, double delta) {
        return (int) Math.floor(from + (to - from) * MathHelper.clamp(delta, 0, 1));
    }

    /**
     * Linear interpolation between two ints
     * @param from Range from
     * @param to Range to
     * @param delta Range delta
     * @return The interpolated value between from and to
     */
    public static double lerp(double from, double to, double delta) {
        return (from + (to - from) * MathHelper.clamp(delta, 0, 1));
    }

    /**
     * Linear interpolation between two colors
     * @param a Color range from
     * @param b Color range to
     * @param c Range delta
     * @return The interpolated color
     */
    public static Color lerp(Color a, Color b, double c) {
        return new Color(lerp(a.getRed(), b.getRed(), c), lerp(a.getGreen(), b.getGreen(), c), lerp(a.getBlue(), b.getBlue(), c), lerp(a.getAlpha(), b.getAlpha(), c));
    }

    /**
     * Modifies a color
     * Any of the components can be set to -1 to keep them from the original color
     * @param original The original color
     * @param redOverwrite The new red component
     * @param greenOverwrite The new green component
     * @param blueOverwrite The new blue component
     * @param alphaOverwrite The new alpha component
     * @return The new color
     */
    public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
        return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite, greenOverwrite == -1 ? original.getGreen() : greenOverwrite, blueOverwrite == -1 ? original.getBlue() : blueOverwrite, alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
    }
}
