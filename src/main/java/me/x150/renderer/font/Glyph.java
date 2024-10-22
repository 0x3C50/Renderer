package me.x150.renderer.font;

public record Glyph(double tlU, double tlV,
					double texW, double texH,
					double baselineX, double baselineY,
					float ascent, float descent,

					int logicalWidth, int logicalHeight,
					char value, GlyphMap owner) {
}
