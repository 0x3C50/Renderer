package me.x150.renderer.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.color.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The rendering class for the 3rd dimension, used in world context
 */
public class Renderer3d {
    /**
     * An empty matrix stack
     */
    private static final MatrixStack empty = new MatrixStack();

    /**
     * The holding list for fading blocks
     */
    private static final List<FadingBlock> fades = new CopyOnWriteArrayList<>();

    /**
     * Reference to the minecraft client
     */
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final AtomicBoolean renderThroughWalls = new AtomicBoolean(false);

    /**
     * Returns an empty matrix stack without having to reinitialize the object
     *
     * @return The empty matrix stack
     */
    public static MatrixStack getEmptyMatrixStack() {
        empty.loadIdentity(); // essentially clear the stack
        return empty;
    }

    /**
     * Renders a fading block that becomes transparent as time progresses
     *
     * @param outlineColor Which color the outline of the block should have
     * @param fillColor    Which color the filling of the block should have
     * @param start        Where the block is placed
     * @param dimensions   The dimensions of the block
     * @param lifeTimeMs   How long the block should be visible for, in milliseconds
     */
    public static void renderFadingBlock(Color outlineColor, Color fillColor, Vec3d start, Vec3d dimensions, long lifeTimeMs) {
        FadingBlock fb = new FadingBlock(outlineColor, fillColor, start, dimensions, System.currentTimeMillis(), lifeTimeMs);

        fades.removeIf(fadingBlock -> fadingBlock.start.equals(start) && fadingBlock.dimensions.equals(dimensions));
        fades.add(fb);
    }

    /**
     * Renders all fading blocks. This is called automatically by the library.
     *
     * @param stack The context MatrixStack
     */
    public static void renderFadingBlocks(MatrixStack stack) {
        fades.removeIf(FadingBlock::isDead);
        for (FadingBlock fade : fades) {
            if (fade == null) {
                continue;
            }
            long lifetimeLeft = fade.getLifeTimeLeft();
            double progress = lifetimeLeft / (double) fade.lifeTime;
            Color out = RendererUtils.modify(fade.outline, -1, -1, -1, (int) (fade.outline.getAlpha() * progress));
            Color fill = RendererUtils.modify(fade.fill, -1, -1, -1, (int) (fade.fill.getAlpha() * progress));
            renderBlockWithEdges(fade.start, fade.dimensions, fill, out).drawAllWithoutVbo(stack);
        }
    }

    /**
     * Renders a block with edges being visible
     *
     * @param start      The start coordinate of the block
     * @param dimensions The dimensions of the blockk
     * @param colorFill  The color the block should be filled with
     * @param colorEdges The color of the outline
     *
     * @return The action batch
     */
    public static RenderActionBatch renderBlockWithEdges(Vec3d start, Vec3d dimensions, Color colorFill, Color colorEdges) {
        return renderBlockWithEdges(new Box(start, start.add(dimensions)), colorFill, colorEdges);
    }

    public static RenderActionBatch renderBlockWithEdges(Box box, Color colorFill, Color colorEdges) {
        RenderAction actionFill = renderFilled(box, colorFill);
        RenderAction actionEdges = renderOutline(box, colorEdges);
        return new RenderActionBatch(actionFill, actionEdges);
    }

    /**
     * Renders a filled block
     *
     * @param start      The start coordinate of the block
     * @param dimensions The dimensions of the block
     * @param color      The color of the filling
     *
     * @return The render action
     */
    public static RenderAction renderFilled(Vec3d start, Vec3d dimensions, Color color) {
        return renderFilled(new Box(start, start.add(dimensions)), color);
    }

    /**
     * Renders a filled block
     *
     * @param box   Contains start and end coordinates of the block
     * @param color The color of the filling
     *
     * @return The render action
     */
    public static RenderAction renderFilled(Box box, Color color) {
        double x1 = box.minX;
        double y1 = box.minY;
        double z1 = box.minZ;
        double x2 = box.maxX;
        double y2 = box.maxY;
        double z2 = box.maxZ;

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();

        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();

        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();

        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();

        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).next();

        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).next();


        return new RenderAction(buffer.end(), GameRenderer.getPositionColorProgram());
    }

    /**
     * Renders an outline of a block
     *
     * @param start      The start coordinate of the block
     * @param dimensions The dimensions of the block
     * @param color      The color of the outline
     *
     * @return The render action
     */
    public static RenderAction renderOutline(Vec3d start, Vec3d dimensions, Color color) {
        return renderOutline(new Box(start, start.add(dimensions)), color);
    }

    /**
     * Renders an outline of a block
     *
     * @param box   Contains start and end coordinates of the block
     * @param color The color of the outline
     *
     * @return The render action
     */
    public static RenderAction renderOutline(Box box, Color color) {
        double x1 = box.minX;
        double y1 = box.minY;
        double z1 = box.minZ;
        double x2 = box.maxX;
        double y2 = box.maxY;
        double z2 = box.maxZ;

        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).next();
        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).normal(0.0F, 1.0F, 0.0F).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).normal(0.0F, 1.0F, 0.0F).next();

        buffer.vertex(x1, y1, z1).color(red, green, blue, alpha).normal(0.0F, 0.0F, 1.0F).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).normal(0.0F, 0.0F, 1.0F).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).normal(0.0F, 1.0F, 0.0F).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).normal(0.0F, 1.0F, 0.0F).next();

        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).normal(-1.0F, 0.0F, 0.0F).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).normal(-1.0F, 0.0F, 0.0F).next();
        buffer.vertex(x1, y2, z1).color(red, green, blue, alpha).normal(0.0F, 0.0F, 1.0F).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).normal(0.0F, 0.0F, 1.0F).next();

        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).normal(0.0F, -1.0F, 0.0F).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).normal(0.0F, -1.0F, 0.0F).next();
        buffer.vertex(x1, y1, z2).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).next();
        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).next();

        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).normal(0.0F, 0.0F, -1.0F).next();
        buffer.vertex(x2, y1, z1).color(red, green, blue, alpha).normal(0.0F, 0.0F, -1.0F).next();
        buffer.vertex(x1, y2, z2).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).normal(1.0F, 0.0F, 0.0F).next();

        buffer.vertex(x2, y1, z2).color(red, green, blue, alpha).normal(0.0F, 1.0F, 0.0F).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).normal(0.0F, 1.0F, 0.0F).next();
        buffer.vertex(x2, y2, z1).color(red, green, blue, alpha).normal(0.0F, 0.0F, 1.0F).next();
        buffer.vertex(x2, y2, z2).color(red, green, blue, alpha).normal(0.0F, 0.0F, 1.0F).next();

        return new RenderAction(buffer.end(), GameRenderer.getRenderTypeLinesProgram());
    }

    /**
     * Renders a straight line between two points
     *
     * @param start The start coordinate of the line
     * @param end   The end coordinate of the line
     * @param color The color of the line
     *
     * @return The render action
     */
    public static RenderAction renderLine(Vec3d start, Vec3d end, Color color) {
        float[] colorFloat = Colors.intArrayToFloatArray(Colors.RGBAIntToRGBA(color.toRGBAInt()));

        float x1 = (float) start.x;
        float y1 = (float) start.y;
        float z1 = (float) start.z;
        float x2 = (float) end.x;
        float y2 = (float) end.y;
        float z2 = (float) end.z;

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y1, z1).color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3]).next();
        buffer.vertex(x2, y2, z2).color(colorFloat[0], colorFloat[1], colorFloat[2], colorFloat[3]).next();

        return new RenderAction(buffer.end(), GameRenderer.getPositionColorProgram());
    }

    /**
     * Gets the world position the crosshair is in. Used for rendering a tracer line from the crosshair to a specific coordinate
     *
     * @return The vec3d describing the crosshair's position
     */
    public static Vec3d getCrosshairVector() {
        Camera camera = client.gameRenderer.getCamera();

        float vec = 0.017453292F;
        float pi = (float) Math.PI;

        float f1 = MathHelper.cos(-camera.getYaw() * vec - pi);
        float f2 = MathHelper.sin(-camera.getYaw() * vec - pi);
        float f3 = -MathHelper.cos(-camera.getPitch() * vec);
        float f4 = MathHelper.sin(-camera.getPitch() * vec);

        return new Vec3d(f2 * f3, f4, f1 * f3).add(camera.getPos());
    }

    public static void setAppropiateGlMode() {
        RenderSystem.depthFunc(rendersThroughWalls() ? GL11.GL_ALWAYS : GL11.GL_LEQUAL);
    }

    /**
     * Tells the renderer to render through any walls or entities<br>
     * Use {@link #stopRenderingThroughWalls()} to revert<br>
     * Use {@link #rendersThroughWalls()} to query current state<br>
     *
     * @see #stopRenderingThroughWalls()
     */
    public static void startRenderingThroughWalls() {
        renderThroughWalls.set(true);
    }

    /**
     * Tells the renderer to stop rendering through walls or entities<br>
     * Use {@link #startRenderingThroughWalls()} to revert<br>
     * Use {@link #rendersThroughWalls()} to query current state<br>
     * Rendering while this flag is unset might result in Z-Fighting
     *
     * @see #startRenderingThroughWalls()
     */
    public static void stopRenderingThroughWalls() {
        renderThroughWalls.set(false);
    }

    /**
     * Returns true if the renderer is currently set to render through walls
     *
     * @return If the renderer is rendering through walls or not
     * @see #startRenderingThroughWalls()
     * @see #stopRenderingThroughWalls()
     */
    public static boolean rendersThroughWalls() {
        return renderThroughWalls.get();
    }

    /**
     * A fading block
     */
    record FadingBlock(Color outline, Color fill, Vec3d start, Vec3d dimensions, long created, long lifeTime) {
        long getLifeTimeLeft() {
            return Math.max(0, (created - System.currentTimeMillis()) + lifeTime);
        }

        boolean isDead() {
            return getLifeTimeLeft() == 0;
        }
    }
}
