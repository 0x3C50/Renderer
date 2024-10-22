package me.x150.renderer.font;

import java.awt.*;

public class FontMetricsAccessor {
	private static final Canvas canvas = new Canvas();

	public static FontMetrics getMetrics(Font f) {
		return canvas.getFontMetrics(f);
	}
}
