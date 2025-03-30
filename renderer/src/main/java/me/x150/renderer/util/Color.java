package me.x150.renderer.util;

import lombok.With;
import net.minecraft.util.math.ColorHelper;

import static net.minecraft.util.math.ColorHelper.*;

public record Color(@With float red, @With float green, @With float blue, @With float alpha) {
	public Color(int argb) {
		this(getRedFloat(argb), getGreenFloat(argb), getBlueFloat(argb), getAlphaFloat(argb));
	}

	public Color(java.awt.Color awtColor) {
		this(awtColor.getRed() / 255f, awtColor.getGreen() / 255f, awtColor.getBlue() / 255f, awtColor.getAlpha() / 255f);
	}

	public int toARGB() {
		return getArgb(channelFromFloat(alpha), channelFromFloat(red), channelFromFloat(green), channelFromFloat(blue));
	}

	public Color lerp(Color other, float f) {
		return new Color(ColorHelper.lerp(f, this.toARGB(), other.toARGB()));
	}

	public java.awt.Color toAwtColor() {
		return new java.awt.Color(red, green, blue, alpha);
	}
}
