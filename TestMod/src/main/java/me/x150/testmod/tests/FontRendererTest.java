package me.x150.testmod.tests;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import me.x150.renderer.fontng.FTLibrary;
import me.x150.renderer.fontng.Font;
import me.x150.renderer.fontng.FontScalingRegistry;
import me.x150.renderer.fontng.GlyphBuffer;

public class FontRendererTest extends BaseComponent {
	private boolean dirty = true;
	private GlyphBuffer gb;
	private FTLibrary ftl;
	private Font font;
	@Override
	public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
		if (dirty) init();
		gb.draw(context, x, y);
	}

	private void init() {
		dirty = false;
		ftl = new FTLibrary();
		font = new Font(ftl, "Rubik.ttf", 0, 10);
		gb = new GlyphBuffer();
		FontScalingRegistry.register(font);
		gb.addString(font, "This is a test string.", 0, 0);
		float h = font.unscaledHeight();
		gb.addString(font, "glDrawElements(123)", 0, h);
		gb.addString(font, "glDrawElements(123)", 0, h*2);
		gb.addString(font, "glDrawElements(123)", 0, h*3);
		for (int i = 0; i < 50; i++) {
			gb.addString(font, String.format("%02x", i).repeat(100), 0, (i + 4) * h);
		}
		gb.offsetToTopLeft();
	}
}
