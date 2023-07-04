package me.x150.renderer.util;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>Utils for rendering in minecraft</p>
 */
@SuppressWarnings("unused")
public class RendererUtils {
	@ApiStatus.Internal
	public static final Matrix4f lastProjMat = new Matrix4f();
	@ApiStatus.Internal
	public static final Matrix4f lastModMat = new Matrix4f();
	@ApiStatus.Internal
	public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

	private static final FastMStack empty = new FastMStack();
	private static final MinecraftClient client = MinecraftClient.getInstance();
	private static final char RND_START = 'a';
	private static final char RND_END = 'z';
	private static final Random RND = new Random();

	/**
	 * <p>Sets up rendering and resets everything that should be reset</p>
	 */
	public static void setupRender() {
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}

	/**
	 * <p>Reverts everything back to normal after rendering</p>
	 */
	public static void endRender() {
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
	}

	/**
	 * <p>Linear interpolation between two integers</p>
	 *
	 * @param from  Range from
	 * @param to    Range to
	 * @param delta Range delta
	 * @return The interpolated value between from and to
	 */
	public static int lerp(int from, int to, double delta) {
		return (int) Math.floor(from + (to - from) * MathHelper.clamp(delta, 0, 1));
	}

	/**
	 * <p>Linear interpolation between two doubles</p>
	 *
	 * @param from  Range from
	 * @param to    Range to
	 * @param delta Range delta
	 * @return The interpolated value between from and to
	 */
	public static double lerp(double from, double to, double delta) {
		return from + (to - from) * MathHelper.clamp(delta, 0, 1);
	}

	/**
	 * <p>Linear interpolation between two colors</p>
	 *
	 * @param a Color range from
	 * @param b Color range to
	 * @param c Range delta
	 * @return The interpolated color
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	public static Color lerp(@NonNull Color a, @NonNull Color b, double c) {
		return new Color(lerp(a.getRed(), b.getRed(), c), lerp(a.getGreen(), b.getGreen(), c),
				lerp(a.getBlue(), b.getBlue(), c), lerp(a.getAlpha(), b.getAlpha(), c));
	}

	/**
	 * <p>Modifies a color</p>
	 * <p>Any of the components can be set to -1 to keep them from the original color</p>
	 *
	 * @param original       The original color
	 * @param redOverwrite   The new red component
	 * @param greenOverwrite The new green component
	 * @param blueOverwrite  The new blue component
	 * @param alphaOverwrite The new alpha component
	 * @return The new color
	 */
	@Contract(value = "_, _, _, _, _ -> new", pure = true)
	public static Color modify(@NonNull Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
		return new Color(
				redOverwrite == -1 ? original.getRed() : redOverwrite,
				greenOverwrite == -1 ? original.getGreen() : greenOverwrite,
				blueOverwrite == -1 ? original.getBlue() : blueOverwrite,
				alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite
		);
	}

	/**
	 * <p>Translates a Vec3d's position with a MatrixStack</p>
	 *
	 * @param stack The MatrixStack to translate with
	 * @param in    The Vec3d to translate
	 * @return The translated Vec3d
	 */
	@Contract(value = "_, _ -> new", pure = true)
	public static Vec3d translateVec3dWithMatrixStack(@NonNull MatrixStack stack, @NonNull Vec3d in) {
		Matrix4f matrix = stack.peek().getPositionMatrix();
		Vector4f vec = new Vector4f((float) in.x, (float) in.y, (float) in.z, 1);
		vec.mul(matrix);
		return new Vec3d(vec.x(), vec.y(), vec.z());
	}

	/**
	 * <p>Registers a BufferedImage as Identifier, to be used in future render calls</p>
	 * <p><strong>WARNING:</strong> This will wait for the main tick thread to register the texture, keep in mind that the texture will not be available instantly</p>
	 * <p><strong>WARNING 2:</strong> This will throw an exception when called when the OpenGL context is not yet made</p>
	 *
	 * @param i  The identifier to register the texture under
	 * @param bi The BufferedImage holding the texture
	 */
	public static void registerBufferedImageTexture(@NonNull Identifier i, @NonNull BufferedImage bi) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", out);
			byte[] bytes = out.toByteArray();

			ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
			data.flip();
			NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
			MinecraftClient.getInstance()
					.execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(i, tex));
		} catch (Exception e) { // should never happen, but just in case
			e.printStackTrace();
		}
	}

	/**
	 * Gets an empty matrix stack without having to initialize the object
	 *
	 * @return An empty matrix stack
	 */
	public static MatrixStack getEmptyMatrixStack() {
		if (!empty.isEmpty()) {
			throw new IllegalStateException(
					"Supposed \"empty\" stack is not actually empty; someone does not clean up after themselves.");
		}
		empty.loadIdentity(); // reset top to identity, in case someone modified it
		return empty;
	}

	/**
	 * Gets the position of the crosshair of the player, transformed into world space
	 *
	 * @return The position of the crosshair of the player, transformed into world space
	 */
	@Contract("-> new")
	public static Vec3d getCrosshairVector() {
		Camera camera = client.gameRenderer.getCamera();

		float pi = (float) Math.PI;
		float yawRad = (float) Math.toRadians(-camera.getYaw());
		float pitchRad = (float) Math.toRadians(-camera.getPitch());
		float f1 = MathHelper.cos(yawRad - pi);
		float f2 = MathHelper.sin(yawRad - pi);
		float f3 = -MathHelper.cos(pitchRad);
		float f4 = MathHelper.sin(pitchRad);

		return new Vec3d(f2 * f3, f4, f1 * f3).add(camera.getPos());
	}

	/**
	 * Transforms an input position into a (x, y, d) coordinate, transformed to screen space. d specifies the far plane of the position, and can be used to check if the position is on screen. Use {@link #screenSpaceCoordinateIsVisible(Vec3d)}.
	 * Example:
	 * <pre>
	 * {@code
	 * // Hud render event
	 * Vec3d targetPos = new Vec3d(100, 64, 100); // world space
	 * Vec3d screenSpace = RendererUtils.worldSpaceToScreenSpace(targetPos);
	 * if (RendererUtils.screenSpaceCoordinateIsVisible(screenSpace)) {
	 *     // do something with screenSpace.x and .y
	 * }
	 * }
	 * </pre>
	 *
	 * @param pos The world space coordinates to translate
	 * @return The (x, y, d) coordinates
	 * @throws NullPointerException If {@code pos} is null
	 */
	@Contract(value = "_ -> new", pure = true)
	public static Vec3d worldSpaceToScreenSpace(@NonNull Vec3d pos) {
		Camera camera = client.getEntityRenderDispatcher().camera;
		int displayHeight = client.getWindow().getHeight();
		int[] viewport = new int[4];
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		Vector3f target = new Vector3f();

		double deltaX = pos.x - camera.getPos().x;
		double deltaY = pos.y - camera.getPos().y;
		double deltaZ = pos.z - camera.getPos().z;

		Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(
				lastWorldSpaceMatrix);

		Matrix4f matrixProj = new Matrix4f(lastProjMat);
		Matrix4f matrixModel = new Matrix4f(lastModMat);

		matrixProj.mul(matrixModel)
				.project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport,
						target);

		return new Vec3d(target.x / client.getWindow().getScaleFactor(),
				(displayHeight - target.y) / client.getWindow().getScaleFactor(), target.z);
	}

	/**
	 * Checks if a screen space coordinate (x, y, d) is on screen
	 *
	 * @param pos The (x, y, d) coordinates to check
	 * @return True if the coordinates are visible
	 */
	public static boolean screenSpaceCoordinateIsVisible(Vec3d pos) {
		return pos != null && pos.z > -1 && pos.z < 1;
	}

	/**
	 * Converts a (x, y, d) screen space coordinate back into a world space coordinate. Example:
	 * <pre>
	 * {@code
	 * // World render event
	 * Vec3d near = RendererUtils.screenSpaceToWorldSpace(100, 100, 0);
	 * Vec3d far = RendererUtils.screenSpaceToWorldSpace(100, 100, 1);
	 * // Ray-cast from near to far to get block or entity at (100, 100) screen space
	 * }
	 * </pre>
	 *
	 * @param x x
	 * @param y y
	 * @param d d
	 * @return The world space coordinate
	 */
	@Contract(value = "_,_,_ -> new", pure = true)
	public static Vec3d screenSpaceToWorldSpace(double x, double y, double d) {
		Camera camera = client.getEntityRenderDispatcher().camera;
		int displayHeight = client.getWindow().getScaledHeight();
		int displayWidth = client.getWindow().getScaledWidth();
		int[] viewport = new int[4];
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
		Vector3f target = new Vector3f();

		Matrix4f matrixProj = new Matrix4f(lastProjMat);
		Matrix4f matrixModel = new Matrix4f(lastModMat);

		matrixProj.mul(matrixModel)
				.mul(lastWorldSpaceMatrix)
				.unproject((float) x / displayWidth * viewport[2],
						(float) (displayHeight - y) / displayHeight * viewport[3], (float) d, viewport, target);

		return new Vec3d(target.x, target.y, target.z).add(camera.getPos());
	}

	/**
	 * Returns the GUI scale of the current window
	 *
	 * @return The GUI scale of the current window
	 */
	public static int getGuiScale() {
		return (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
	}

	private static String randomString(int length) {
		return IntStream.range(0, length)
				.mapToObj(operand -> String.valueOf((char) RND.nextInt(RND_START, RND_END + 1)))
				.collect(Collectors.joining());
	}

	/**
	 * Returns an identifier in the renderer namespace, with a random id
	 *
	 * @return The identifier
	 */
	@Contract(value = "-> new", pure = true)
	public static Identifier randomIdentifier() {
		return new Identifier("renderer", "temp/" + randomString(32));
	}
}
