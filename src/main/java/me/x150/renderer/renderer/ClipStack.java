package me.x150.renderer.renderer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vector4f;

import java.util.Deque;
import java.util.Stack;


/**
 * A class used for defining clipping rectangles
 */
@SuppressWarnings("deprecation") // deprecated for internal use only
public class ClipStack {
    public static final ClipStack globalInstance = new ClipStack();
    final Stack<TransformationEntry> clipStack = new Stack<>();

    /**
     * Adds a clipping window to the stack<br>
     * All new rendered elements will only be rendered if they conform to this rectangle and the others above it<br>
     * This function takes the old clip stack into account<br>
     * <strong>Always call {@link #popWindow()} after you're done rendering with this</strong>
     * @param stack The context MatrixStack
     * @param r1 The new clipping rectangle to enlist
     */
    public void addWindow(MatrixStack stack, Rectangle r1) {
        Matrix4f matrix = stack.peek().getPositionMatrix();
        Vector4f coord = new Vector4f((float) r1.getX(), (float) r1.getY(), 0, 1);
        Vector4f end = new Vector4f((float) r1.getX1(), (float) r1.getY1(), 0, 1);
        coord.transform(matrix);
        end.transform(matrix);
        double x = coord.getX();
        double y = coord.getY();
        double endX = end.getX();
        double endY = end.getY();
        Rectangle r = new Rectangle(x, y, endX, endY);
        if (clipStack.empty()) {
            clipStack.push(new TransformationEntry(r, stack.peek()));
            Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
        } else {
            Rectangle lastClip = clipStack.peek().rect;
            double lsx = lastClip.getX();
            double lsy = lastClip.getY();
            double lstx = lastClip.getX1();
            double lsty = lastClip.getY1();
            double nsx = MathHelper.clamp(r.getX(), lsx, lstx);
            double nsy = MathHelper.clamp(r.getY(), lsy, lsty);
            double nstx = MathHelper.clamp(r.getX1(), nsx, lstx);
            double nsty = MathHelper.clamp(r.getY1(), nsy, lsty); // totally intended varname
            clipStack.push(new TransformationEntry(new Rectangle(nsx, nsy, nstx, nsty), stack.peek()));
            Renderer2d.beginScissor(nsx, nsy, nstx, nsty);
        }
    }

    /**
     * Pops the latest added window from the stack
     */
    public void popWindow() {
        clipStack.pop();
        if (clipStack.empty()) {
            Renderer2d.endScissor();
        } else {
            TransformationEntry r1 = clipStack.peek();
            Rectangle r = r1.rect;
            Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());

        }
    }

    /**
     * Renders something outside of the currently applied clipping rectangle stackk
     * @param e The runnable to run outside the clip stack
     */
    public void renderOutsideClipStack(Runnable e) {
        if (clipStack.empty()) {
            e.run();
        } else {
            Renderer2d.endScissor();
            e.run();
            Rectangle r = clipStack.peek().rect;
            Renderer2d.beginScissor(r.getX(), r.getY(), r.getX1(), r.getY1());
        }
    }

    record TransformationEntry(Rectangle rect, MatrixStack.Entry transformationEntry) {
    }
}

