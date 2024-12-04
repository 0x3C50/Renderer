package me.x150.renderer.render;

import me.x150.renderer.util.Rectangle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * Defines and manages clipping "windows", that are able to intersect. If a window is created, an opengl scissor window is created,
 * and all subsequent windows being created will be clamped into the previous. This means, a window {@code Wn} will always be fully contained within all windows {@code W&lt;n}, except for window {@code W0}.
 */
public class ClipStack {
	static final Deque<Rectangle> clipStack = new ArrayDeque<>();

	/**
	 * Adds and applies a new clipping rectangle.
	 * <ol>
	 *     <li>The bounds are constrained to the previous rectangle, if present</li>
	 *     <li>A new opengl scissor rectangle is created and applied</li>
	 *     <li>The new rectangle, with updated bounds, is added to the stack</li>
	 * </ol>
	 * To prevent a memory leak (and unintended behavior), call {@link #popWindow()} afterwards.
	 * @param stack MatrixStack to transform rectangle with
	 * @param rect Rectangle defining the bounds of the new clipping window. The clipping window MAY be smaller than the bounds specified.
	 */
	public static void addWindow(MatrixStack stack, Rectangle rect) {
		Matrix4f matrix = stack.peek().getPositionMatrix();
		Vector4f start = new Vector4f((float) rect.getX(), (float) rect.getY(), 0, 1);
		Vector4f end = new Vector4f((float) rect.getX1(), (float) rect.getY1(), 0, 1);
		start.mul(matrix);
		end.mul(matrix);
		double x0 = start.x();
		double y0 = start.y();
		double x1 = end.x();
		double y1 = end.y();
		Rectangle transformed = new Rectangle(x0, y0, x1, y1);
		if (clipStack.isEmpty()) {
			clipStack.push(transformed);
			Renderer2d.beginScissor(transformed.getX(), transformed.getY(), transformed.getX1(), transformed.getY1());
		} else {
			Rectangle lastClip = clipStack.peek();
			double lx0 = lastClip.getX();
			double ly0 = lastClip.getY();
			double lx1 = lastClip.getX1();
			double ly1 = lastClip.getY1();
			double nx0 = MathHelper.clamp(transformed.getX(), lx0, lx1);
			double ny0 = MathHelper.clamp(transformed.getY(), ly0, ly1);
			double nx1 = MathHelper.clamp(transformed.getX1(), nx0, lx1);
			double ny1 = MathHelper.clamp(transformed.getY1(), ny0, ly1);
			clipStack.push(new Rectangle(nx0, ny0, nx1, ny1));
			Renderer2d.beginScissor(nx0, ny0, nx1, ny1);
		}
	}

	/**
	 * Adds a window using {@link #addWindow(MatrixStack, Rectangle)}, calls renderAction, then removes the previously added window automatically.
	 * You can replace this by separate {@link #addWindow(MatrixStack, Rectangle)} and {@link #popWindow()} calls, although using this method will do that for you.
	 *
	 * @param stack        The context MatrixStack
	 * @param clippingRect The clipping rectangle that should be applied to the renderAction
	 * @param renderAction The actual render method, that renders the content
	 */
	public static void use(MatrixStack stack, Rectangle clippingRect, Runnable renderAction) {
		addWindow(stack, clippingRect);
		renderAction.run();
		popWindow();
	}

	/**
	 * Pops the top from the stack, and either removes the opengl scissor window (if no more stack elements are present), or moves the opengl scissor window to the new top.
	 */
	public static void popWindow() {
		clipStack.pop();
		if (clipStack.isEmpty()) {
			Renderer2d.endScissor();
		} else {
			Rectangle r = clipStack.peek();
			Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
		}
	}

	/**
	 * Runs {@code e} outside of the currently applied scissor window. If the stack is empty, the action is executed. If it isn't empty, the current opengl scissor window is removed, {@code e} is executed, and the scissor window is reapplied.
	 *
	 * @param e The runnable to run outside the clip stack
	 */
	public static void renderOutsideClipStack(Runnable e) {
		if (clipStack.isEmpty()) {
			e.run();
		} else {
			Renderer2d.endScissor();
			e.run();
			Rectangle r = clipStack.peek();
			Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
		}
	}

}

