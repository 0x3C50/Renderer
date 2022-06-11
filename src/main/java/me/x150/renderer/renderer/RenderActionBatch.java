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
     * <p>Draws all actions</p>
     * <b>This indicates that all actions will be used multiple times</b>
     *
     * @param stack The context MatrixStack
     */
    public void drawAll(MatrixStack stack) {
        doForAll(renderAction -> {
            renderAction.draw(stack);
        });
    }

    /**
     * <p>Draws all actions once</p>
     * <b>This will delete all buffers after rendering</b>
     * <b>Indicates that this action is to be rendered once</b>
     *
     * @param stack The context MatrixStack
     */
    public void drawAllOnce(MatrixStack stack) {
        doForAll(renderAction -> {
            renderAction.drawOnce(stack);
        });
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
