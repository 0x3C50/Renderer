package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.fontng.*;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;

public class Handler {
	private static boolean inited = false;

	private static Font font, emojiFont;
	private static GlyphBuffer gb;

	private static FTLibrary ftl;

	public static void world(MatrixStack stack) {

	}

	@SneakyThrows
	public static void hud(DrawContext context) {
		if (!inited) {
			inited = true;
			ftl = new FTLibrary();

			font = new Font(ftl, "Roboto-Regular.ttf", 0, 20);

			emojiFont = new Font(ftl, "mt.ttf", 0, 20);

			FontScalingRegistry.register(font, emojiFont);

			gb = new GlyphBuffer();
		}

		gb.clear();
		gb.addString(emojiFont, "search", 0, 0)
				.then(font, "search for some shit", 5, -3);

		gb.offsetToTopLeft();


		context.fill(99, 99, 101, 101, 0xFFFFFFFF);
//		float mx = 100 - gb.minY;
//		float my = 100 - gb.minX;

		gb.drawDebuggingInformation(context, 100, 100);

		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(1536));
		MatrixStack emp = RendererUtils.getEmptyMatrixStack();
//		emp.push();
		gb.draw(immediate, emp, 100, 100);
//		emp.pop();
		immediate.draw();

//		context.fill((int) my, (int) mx, (int) (my+1), (int) (mx+1), 0xFFFFFFFF);
	}
}
