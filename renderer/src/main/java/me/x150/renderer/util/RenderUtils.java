package me.x150.renderer.util;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.NonNull;
import me.x150.renderer.mixin.NativeImageAccessor;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;

/**
 * <p>Utils for rendering in minecraft</p>
 */
@SuppressWarnings("unused")
public class RenderUtils {
	@ApiStatus.Internal
	public static final Matrix4f lastProjMat = new Matrix4f();
	@ApiStatus.Internal
	public static final Matrix4f lastModMat = new Matrix4f();
	@ApiStatus.Internal
	public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
	@ApiStatus.Internal
	public static final int[] lastViewport = new int[4];

	private static final MatrixStack empty = new MatrixStack();
	private static final MinecraftClient client = MinecraftClient.getInstance();

	/**
	 * <p>Linear interpolation between two integers</p>
	 *
	 * @param from  Range from
	 * @param to    Range to
	 * @param delta Range delta
	 * @return The interpolated value between from and to
	 * @deprecated {@link MathHelper#lerp(float, int, int)}
	 */
	@Deprecated(since = "2.0.0")
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
	 * @deprecated {@link MathHelper#lerp(double, double, double)}
	 */
	@Deprecated(since = "2.0.0")
	public static double lerp(double from, double to, double delta) {
		return from + (to - from) * MathHelper.clamp(delta, 0, 1);
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
	public static NativeImageBackedTexture registerBufferedImageTexture(@NonNull Identifier i, @NonNull BufferedImage bi) {
		NativeImageBackedTexture tex = bufferedImageToNIBT("renderer/bi2nibt/" + i, bi);
		RenderSystem.assertOnRenderThread();
		MinecraftClient.getInstance().getTextureManager().registerTexture(i, tex);
		return tex;
	}

	public static @NotNull NativeImageBackedTexture bufferedImageToNIBT(String textureName, @NotNull BufferedImage bi) {
		// argb from BufferedImage is little endian, alpha is actually where the `a` is in the label
		// rgba from NativeImage (and by extension opengl) is big endian, alpha is on the other side (abgr)
		// thank you opengl
		int ow = bi.getWidth();
		int oh = bi.getHeight();
		NativeImage image = new NativeImage(NativeImage.Format.RGBA, ow, oh, false);
		@SuppressWarnings("DataFlowIssue") long ptr = ((NativeImageAccessor) (Object) image).getPointer();
		IntBuffer backingBuffer = MemoryUtil.memIntBuffer(ptr, image.getWidth() * image.getHeight());
		int off = 0;
		Object _d;
		WritableRaster raster = bi.getRaster();
		ColorModel colorModel = bi.getColorModel();
		int nbands = raster.getNumBands();
		int dataType = raster.getDataBuffer().getDataType();
		_d = switch (dataType) {
			case DataBuffer.TYPE_BYTE -> new byte[nbands];
			case DataBuffer.TYPE_USHORT -> new short[nbands];
			case DataBuffer.TYPE_INT -> new int[nbands];
			case DataBuffer.TYPE_FLOAT -> new float[nbands];
			case DataBuffer.TYPE_DOUBLE -> new double[nbands];
			default -> throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
		};

		for (int y = 0; y < oh; y++) {
			for (int x = 0; x < ow; x++) {
				raster.getDataElements(x, y, _d);
				int a = colorModel.getAlpha(_d);
				int r = colorModel.getRed(_d);
				int g = colorModel.getGreen(_d);
				int b = colorModel.getBlue(_d);
				int abgr = a << 24 | b << 16 | g << 8 | r;
				backingBuffer.put(abgr);
			}
		}
		NativeImageBackedTexture tex = new NativeImageBackedTexture(() -> textureName, image);
		tex.upload();
		return tex;
	}

	/**
	 * Gets an empty matrix stack without having to initialize the object
	 *
	 * @return An empty matrix stack
	 */
	public static MatrixStack getEmptyMatrixStack() {
		if (!empty.isEmpty()) {
			throw new IllegalStateException("Supposed \"empty\" stack is not actually empty; someone does not clean up after themselves.");
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
		Vector3f target = new Vector3f();

		double deltaX = pos.x - camera.getPos().x;
		double deltaY = pos.y - camera.getPos().y;
		double deltaZ = pos.z - camera.getPos().z;

		Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(lastWorldSpaceMatrix);

		Matrix4f matrixProj = new Matrix4f(lastProjMat);
		Matrix4f matrixModel = new Matrix4f(lastModMat);

		matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), lastViewport, target);

		return new Vec3d(target.x / client.getWindow().getScaleFactor(), (displayHeight - target.y) / client.getWindow().getScaleFactor(), target.z);
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
		Vector3f target = new Vector3f();

		Matrix4f matrixProj = new Matrix4f(lastProjMat);
		Matrix4f matrixModel = new Matrix4f(lastModMat);

		matrixProj.mul(matrixModel).mul(lastWorldSpaceMatrix).unproject((float) x / displayWidth * lastViewport[2], (float) (displayHeight - y) / displayHeight * lastViewport[3], (float) d, lastViewport, target);

		return new Vec3d(target.x, target.y, target.z).add(camera.getPos());
	}

	/**
	 * Returns the GUI scale of the current window
	 *
	 * @return The GUI scale of the current window
	 */
	public static int getGuiScale() {
		return MinecraftClient.getInstance().getWindow().getScaleFactor();
	}
}
