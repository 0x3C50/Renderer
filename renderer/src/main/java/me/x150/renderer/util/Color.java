package me.x150.renderer.util;

import lombok.With;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Range;

import static net.minecraft.util.math.ColorHelper.*;

/**
 * A color, described by 4 floats r, g, b, a, in the range of [0, 1]
 * @param red Red component
 * @param green Green component
 * @param blue Blue component
 * @param alpha Alpha component
 */
public record Color(@With @Range(from=0, to=1) float red, @With @Range(from=0, to=1) float green, @With @Range(from=0, to=1) float blue, @With @Range(from=0, to=1) float alpha) {
	/**
	 * Creates a color from an argb integer. Alpha is NOT automatically filled. If you use {@code 0xFFFFFF}, alpha will be 0!
	 * @param argb ARGB integer describing color
	 */
	public Color(int argb) {
		this(getRedFloat(argb), getGreenFloat(argb), getBlueFloat(argb), getAlphaFloat(argb));
	}

	/**
	 * Creates a color from a {@link java.awt.Color}
	 * @param awtColor AWT color
	 */
	public Color(java.awt.Color awtColor) {
		this(awtColor.getRed() / 255f, awtColor.getGreen() / 255f, awtColor.getBlue() / 255f, awtColor.getAlpha() / 255f);
	}

	/**
	 * Gets this color as an ARGB integer.
	 * @return ARGB integer describing this color
	 */
	public int toARGB() {
		return getArgb(channelFromFloat(alpha), channelFromFloat(red), channelFromFloat(green), channelFromFloat(blue));
	}

	/**
	 * Interpolates between this color and another, based on lerp factor f.
	 * @param other Other color to interpolate between
	 * @param f Usually [0, 1], 0 = this color, 1 = other color. (0, 1) = some mix between this and other color
	 * @return Lerped color
	 */
	public Color lerp(Color other, float f) {
		return new Color(ColorHelper.lerp(f, this.toARGB(), other.toARGB()));
	}

	/**
	 * Gets a {@link java.awt.Color} with these contents.
	 * @return new awt Color
	 */
	public java.awt.Color toAwtColor() {
		return new java.awt.Color(red, green, blue, alpha);
	}
}
