package me.x150.renderer.renderer;

import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;

/**
 * A batch of render actions
 *
 * @param actions The actions to batch
 */
public record RenderActionBatch(RenderAction... actions) {
    /**
     * <p>Draws all actions by creating a new VBO</p>
     *
     * @see RenderAction#drawWithVBO(MatrixStack) 
     * @param stack The context MatrixStack
     */
    public void drawAllWithVbo(MatrixStack stack) {
        doForAll(renderAction -> renderAction.drawWithVBO(stack));
    }

    /**
     * <p>Draws all actions without creating a new VBO</p>
     *
     * @see RenderAction#drawWithoutVBO(MatrixStack)
     * @param stack The context MatrixStack
     */
    public void drawAllWithoutVbo(MatrixStack stack) {
        doForAll(renderAction -> renderAction.drawWithoutVBO(stack));
    }

    /**
     * Does something for all buffers in this batch
     *
     * @param action The action to complete
     */
    public void doForAll(Consumer<RenderAction> action) {
        for (RenderAction renderAction : actions) {
            action.accept(renderAction);
        }
    }
}
