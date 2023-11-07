package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler {
//	static FontRenderer title;
//	static FontRenderer main;
	static FontRenderer fr;
	private static ObjFile ob;

	@SneakyThrows
	public static void world(MatrixStack stack) {
		if (ob == null) {
			ob = new ObjFile("untitled.obj",
					ObjFile.ResourceProvider.ofPath(Path.of("/media/x150/stuff/Dev/Java/RenderLib2/run/")));
		}
		ob.draw(stack, new Matrix4f(), new Vec3d(0, 400, 0));
		OutlineFramebuffer.useAndDraw(() -> Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 300, 0), new Vec3d(5, 5, 5)), 1f, Color.GREEN, Color.BLACK);
	}

	public static MutableText gradient(Text original, int from, int to) {
		MutableText root = Text.empty();
		int len = original.getString().length();
		AtomicInteger idx = new AtomicInteger();
		float aR = (from >> 16 & 0xFF) / 255f;
		float aG = (from >> 8 & 0xFF) / 255f;
		float aB = (from & 0xFF) / 255f;
		float bR = (to >> 16 & 0xFF) / 255f;
		float bG = (to >> 8 & 0xFF) / 255f;
		float bB = (to & 0xFF) / 255f;
		original.asOrderedText().accept((index, style, codePoint) -> {
			float f = (float) idx.getAndIncrement() / (len-1);
			System.out.println(f);
			MutableText text = Text.literal(String.valueOf((char) codePoint));
			int c = (int) ((aR + f * (bR - aR)) * 255) << 16
					| (int) ((aG + f * (bG - aG)) * 255) << 8
					| (int) ((aB + f * (bB - aB)) * 255);
			text.setStyle(style.withColor(c));
			root.append(text);
			return true;
		});
		return root;
	}

	public static void main(String[] args) {
		Text.of("h§ci").asOrderedText().accept((index, style, codePoint) -> {
			System.out.println(index+": "+(char) codePoint);
			return true;
		});
		Text fake = gradient(Text.empty()
				.append(Text.literal("te").styled(s -> s.withBold(true)))
				.append(Text.literal("xt").styled(s -> s.withItalic(true))), 0xFF0000, 0x00FF00);
		System.out.println(fake);
	}


	static long l = 0;

	@SneakyThrows
	public static void hud(DrawContext matrices) {
		if (fr == null) {
			Font[] fonts = Font.createFonts(Objects.requireNonNull(Handler.class.getClassLoader().getResourceAsStream("real.ttf")));
			fr = new FontRenderer(fonts, 64, 32, 2);
		}
		if (l++ > 60 * 4) fr.drawString(matrices.getMatrices(), "012345689", 5, 5, 1, 1, 1, 1);
//		if (title == null) {
//			title = new FontRenderer(new Font[]{
//					new Font("Roboto", Font.BOLD, 8)
//			}, 9f * 2);
//			main = new FontRenderer(new Font[]{
//					new Font("Roboto", Font.PLAIN, 8)
//			}, 9f);
//		}
//		MSAAFramebuffer.use(8, () -> {
//			MatrixStack fs = RendererUtils.getEmptyMatrixStack();
//			fs.push();
//			String title = "Name here";
//			String subtext = "Introducing Renderer, best open-source 1.20 rendering library!!";
//			String[] buttons = { "Direct Download", "Source Code" };
//			double maxW = Arrays.stream(buttons).mapToDouble(d -> main.getStringWidth(d)).max().orElseThrow();
//			double entireW = Math.max(Handler.title.getStringWidth(title), main.getStringWidth(subtext));
//			entireW = Math.max(maxW*2 + 10 + 10 + 10, entireW);
//			entireW += 20; // padding
//			float buttonHeight = main.getStringHeight(buttons[0]) + 10;
//			Renderer2d.renderRoundedQuad(fs, new Color(20, 20, 20, 50), 10, 10, 10 + entireW, 10 + 5 + Handler.title.getStringHeight(title) + 5 + main.getStringHeight(subtext) + 5 + buttonHeight + 10, 10, 10);
//			Handler.title.drawCenteredString(fs, title, (float) (10 + entireW / 2f), 10 + 5, 1f, 1f, 1f, 1f);
//			main.drawCenteredString(fs, subtext, (float) (10 + entireW / 2f), 10 + 5 + Handler.title.getStringHeight(subtext) + 5, 1f, 1f, 1f, 1f);
//			double offset = buttons.length / 2d * (maxW + 20) + (buttons.length - 1) / 2d * 5;
//			for (String button : buttons) {
//				Renderer2d.renderRoundedQuad(fs, new Color(60, 60, 60, 255), (10 + entireW / 2f) - offset, 10 + 5 + Handler.title.getStringHeight(subtext) + 5 + main.getStringHeight(subtext) + 5, (10 + entireW / 2f) - offset + (maxW + 20), 10 + 5 + Handler.title.getStringHeight(subtext) + 5 + main.getStringHeight(subtext) + 5 + buttonHeight, buttonHeight/2, 5);
//				main.drawCenteredString(fs, button, (float) ((10 + entireW / 2f) - offset + (maxW + 20) * .5), 10 + 5 + Handler.title.getStringHeight(subtext) + 5 + main.getStringHeight(subtext) + 5 + (buttonHeight - main.getStringHeight(button)) * .5f, 1f, 1f, 1f, 1f);
//				offset -= maxW + 25;
//			}
//			fs.pop();
//		});
	}
}
