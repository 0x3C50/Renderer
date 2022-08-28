package me.x150.renderer.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.renderer.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * <p>Utils for rendering in minecraft</p>
 */
public class RendererUtils {
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
     * Aligns a MatrixStack for rendering
     *
     * @param stack The MatrixStack to align
     */
    static void alignForRendering(MatrixStack stack) {
        Camera c = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d camPos = c.getPos();
        stack.translate(-camPos.x, -camPos.y, -camPos.z);
    }

    /**
     * <p>Linear interpolation between two ints</p>
     *
     * @param from  Range from
     * @param to    Range to
     * @param delta Range delta
     *
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
     *
     * @return The interpolated value between from and to
     */
    public static double lerp(double from, double to, double delta) {
        return (from + (to - from) * MathHelper.clamp(delta, 0, 1));
    }

    /**
     * <p>Linear interpolation between two colors</p>
     *
     * @param a Color range from
     * @param b Color range to
     * @param c Range delta
     *
     * @return The interpolated color
     */
    public static Color lerp(Color a, Color b, double c) {
        return new Color(lerp(a.getRed(), b.getRed(), c), lerp(a.getGreen(), b.getGreen(), c), lerp(a.getBlue(), b.getBlue(), c), lerp(a.getAlpha(), b.getAlpha(), c));
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
     *
     * @return The new color
     */
    public static Color modify(Color original, int redOverwrite, int greenOverwrite, int blueOverwrite, int alphaOverwrite) {
        return new Color(redOverwrite == -1 ? original.getRed() : redOverwrite, greenOverwrite == -1 ? original.getGreen() : greenOverwrite, blueOverwrite == -1 ? original.getBlue() : blueOverwrite, alphaOverwrite == -1 ? original.getAlpha() : alphaOverwrite);
    }

    /**
     * <p>Translates a Vec3d's position with a MatrixStack</p>
     *
     * @param stack The MatrixStack to translate with
     * @param in    The Vec3d to translate
     *
     * @return The translated Vec3d
     */
    public static Vec3d translateVec3dWithMatrixStack(MatrixStack stack, Vec3d in) {
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Vector4f parsedVecf = new Vector4f((float) in.x, (float) in.y, (float) in.z, 1);
        parsedVecf.transform(matrix);
        return new Vec3d(parsedVecf.getX(), parsedVecf.getY(), parsedVecf.getZ());
    }

    /**
     * <p>Registers a BufferedImage as Identifier, to be used in future render calls</p>
     * <p><strong>WARNING:</strong> This will wait for the main tick thread to register the texture, keep in mind that the texture will not be available instantly</p>
     * <p><strong>WARNING 2:</strong> This will throw an exception when called when the OpenGL context is not yet made</p>
     *
     * @param i  The identifier to register the texture under
     * @param bi The BufferedImage holding the texture
     */
    public static void registerBufferedImageTexture(Identifier i, BufferedImage bi) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
            byte[] bytes = baos.toByteArray();

            ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
            data.flip();
            NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(i, tex));
        } catch (Exception e) { // should never happen, but just in case
            e.printStackTrace();
        }
    }
}
