package me.x150.renderer.render;

import me.x150.renderer.util.Rectangle;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Stack;


/**
 * A class used for defining clipping rectangles
 */
public class ClipStack {
	static final Stack<Rectangle> clipStack = new Stack<>();

	/**
	 * <p>Adds a clipping window to the stack</p>
	 * <p>All new rendered elements will only be rendered if they conform to this rectangle and the others above it</p>
	 * <strong>Always call {@link #popWindow()} after you're done rendering with this</strong>
	 *
	 * @param stack The context MatrixStack
	 * @param rect  The new clipping rectangle to enlist
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
		if (clipStack.empty()) {
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
	 * <p></p>
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
	 * <p>Pops the latest added window from the stack</p>
	 */
	public static void popWindow() {
		clipStack.pop();
		if (clipStack.empty()) {
			Renderer2d.endScissor();
		} else {
			Rectangle r = clipStack.peek();
			Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
		}
	}

	/**
	 * <p>Renders something outside of the currently applied clipping rectangle stack</p>
	 *
	 * @param e The runnable to run outside the clip stack
	 */
	public static void renderOutsideClipStack(Runnable e) {
		if (clipStack.empty()) {
			e.run();
		} else {
			Renderer2d.endScissor();
			e.run();
			Rectangle r = clipStack.peek();
			Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
		}
	}

}

