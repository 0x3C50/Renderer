package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.fontng.*;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Handler {
	private static boolean inited = false;

	private static Font font, emojiFont;
	private static GlyphBuffer gb;

	private static FTLibrary ftl;

	public static void world(MatrixStack stack) {

	}

	static float theX;

	public static void main(String[] args) {
// Once, to initialize:
FTLibrary library = new FTLibrary();
Font mainFont = new Font(library, "SomeFont.ttf", 0, 20);
FontScalingRegistry.register(mainFont);

// To shape new text (shouldn't be done every frame if possible, this is expensive!)
GlyphBuffer buffer = new GlyphBuffer();
buffer.addString(mainFont, "Some text: ", 0, 0)
		.then(mainFont, Text.literal("some styled text! ").styled(it -> it.withColor(0xFF0000).withUnderline(true)), 0, 0)
		.then(mainFont, "and some more regular text", 0, 0);
buffer.offsetToTopLeft(); // ensure buffer is viewed from top left coordinate

// to draw text:
VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(new BufferAllocator(1024)); // or some other source
MatrixStack ms = RendererUtils.getEmptyMatrixStack();
buffer.draw(vcp, ms, 100, 100);
vcp.draw();

// cleanup:
mainFont.close();
library.close();
	}

	@SneakyThrows
	public static void hud(DrawContext context) {
		if (!inited) {
			inited = true;
			ftl = new FTLibrary();

			font = new Font(ftl, "Roboto-Regular.ttf", 0, 12);

			emojiFont = new Font(ftl, "NotoEmoji-Regular.ttf", 0, 12);

			FontScalingRegistry.register(font, emojiFont);

			gb = new GlyphBuffer();

			GlyphBuffer.FollowUp row = gb.addString(font, "This is the current time: ", 0, 0);
			theX = row.getPenX();
			gb.addString(font, "and this is an em", 0, font.unscaledHeight())
					.then(emojiFont, Text.literal("\uD83D\uDE02❤\uFE0E\uD83D\uDE0D\uD83E\uDD23\uD83D\uDE0A").styled(it -> it.withColor(0xFFAA00).withStrikethrough(true)), 0, 0)
					.then(font, "oji", 0, 0);
			gb.addText(font, Text.empty()
							.append(Text.literal("aa").styled(it -> it.withUnderline(true)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(false)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(true).withStrikethrough(true)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(true).withStrikethrough(false)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(false).withStrikethrough(true)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(true).withStrikethrough(false).withColor(0xFFAAAA)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(true).withStrikethrough(true).withColor(0xAAFFAA)))
							.append(Text.literal("aa").styled(it -> it.withUnderline(false).withStrikethrough(true).withColor(0xAAFFAA)))
					, 0, font.unscaledHeight()*2);
//			gb.offsetToTopLeft();
		}

		int count = (int) (Math.abs((System.currentTimeMillis() % 5000) / 5000f - 0.5) * 2 * 10) + 1;
		int id = gb.addText(font, Text.literal("a".repeat(count)).styled(it -> it.withUnderline(true).withStrikethrough(true)), theX, 0).getRunId();
		gb.offsetToCenter();

		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(new BufferAllocator(1536));
		MatrixStack emp = RendererUtils.getEmptyMatrixStack();
//		emp.push();
		gb.draw(immediate, emp, 100, 100);
//		emp.pop();
		immediate.draw();

		gb.removeRunId(id);
	}
}
