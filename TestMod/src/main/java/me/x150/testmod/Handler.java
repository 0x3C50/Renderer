package me.x150.testmod;

import lombok.SneakyThrows;
import me.x150.renderer.font.FontRenderer;
import me.x150.renderer.objfile.ObjFile;
import me.x150.renderer.render.OutlineFramebuffer;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.render.Renderer3d;
import me.x150.renderer.util.RendererUtils;
import me.x150.testmod.client.TestModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.awt.*;

public class Handler {
	static FontRenderer fr;
	static FontRenderer fr1;
	private static ObjFile ob;


	/*protected int getBlockLight(T entity, BlockPos pos) {
		return entity.isOnFire() ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, pos);
	}
	public final int getLight(float tickDelta) {
		BlockPos blockPos = BlockPos.ofFloored(entity.getClientCameraPosVec(tickDelta));
		return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.getSkyLight(entity, blockPos));
	}*/

	@SneakyThrows
	public static void world(MatrixStack stack) {
		Vec3d pos = new Vec3d(0, 100, 0);
		BlockPos bp = BlockPos.ofFloored(pos);
		MinecraftClient client = MinecraftClient.getInstance();
		ClientWorld world = client.world;
		if (world != null) {
			// Compute celestial light based on time of day.
			float celestialAngle = world.getSkyAngleRadians(1.0F);
			float celestialLight = 1.0F - (MathHelper.cos(celestialAngle >= Math.PI ? (float)Math.PI * 2 - celestialAngle : celestialAngle) * 2.0F + 0.2F);
			celestialLight = MathHelper.clamp(celestialLight, 0.0F, 1.0F);
			celestialLight = 1.0F - celestialLight;
			celestialLight = (float)((double)celestialLight * ((1.0D - (double)world.getRainGradient(1.0F) * 5.0F / 16.0D)));
			celestialLight = (float)((double)celestialLight * ((1.0D - (double)world.getThunderGradient(1.0F) * 5.0F / 16.0D)));

			// Compute block light.
			int blockLightLevel = world.getLightLevel(LightType.BLOCK, bp);
			float blockLight = blockLightLevel / 15.0F;

			// Combine block light with celestial light.
			float finalLight = Math.max(Math.max(celestialLight, blockLight), 0.2f);

			TestModClient.testObj.draw(stack, new Matrix4f(), pos, finalLight);
			OutlineFramebuffer.useAndDraw(() -> Renderer3d.renderFilled(stack, Color.WHITE, new Vec3d(0, 300, 0), new Vec3d(5, 5, 5)), 1f, Color.GREEN, Color.BLACK);
		}
	}







	public static void hud(DrawContext matrices) {
		if (fr == null) {
			fr = new FontRenderer(new Font[]{
					new Font("Ubuntu", Font.PLAIN, 8)
			}, 9f);
			fr1 = new FontRenderer(new Font[]{
					new Font("Ubuntu", Font.BOLD, 8)
			}, 9f * 3);
		}
		MatrixStack fs = RendererUtils.getEmptyMatrixStack();
		fs.push();
		Renderer2d.renderEllipse(matrices.getMatrices(), Color.RED, 30, 120, 10, 15, 20);
		Renderer2d.renderEllipseOutline(matrices.getMatrices(), Color.RED, 70, 120, 30, 15, 1+(System.currentTimeMillis() % 5000) / 5000d * 10, 1+(1- (System.currentTimeMillis() % 5000) / 5000d) * 10, 20);
		String n = """
				This is a rendering library.
				It supports TTF font rendering.
				I can type äöü, it will render it.
				It also supports newlines.
				""".trim();
		float stringWidth = fr.getStringWidth(n);
		float stringHeight = fr.getStringHeight(n);
//		fs.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees((System.currentTimeMillis() % 5000) / 5000f * 360f),
//				30 + (stringWidth + 5) / 2, 30 + (stringHeight + 5) / 2, 0);
		Renderer2d.renderRoundedQuad(fs, Color.BLACK, 30 - 5, 30 - 5, 30 + stringWidth + 5, 30 + stringHeight + 5, 5,
				5);
		fr.drawString(fs, n, 30, 30, 1f, 1f, 1f, 1f);
		fs.pop();
	}
}
