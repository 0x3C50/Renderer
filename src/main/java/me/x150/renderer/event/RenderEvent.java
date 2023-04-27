package me.x150.renderer.event;

import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Something has been rendered
 */
@Getter
public class RenderEvent {
    /**
     * The MatrixStack used to render the element
     */
    final MatrixStack matrixStack;

    /**
     * Constructs a new RenderEvent
     *
     * @param stack The MatrixStack used to render the element
     */
    public RenderEvent(MatrixStack stack) {
        this.matrixStack = stack;
    }

    /**
     * The hud has been rendered
     */
    public static class Hud extends RenderEvent {
        /**
         * Constructs a new HUD RenderEvent
         *
         * @param stack The MatrixStack used to render the element
         */
        public Hud(MatrixStack stack) {
            super(stack);
        }
    }

    public static class World extends RenderEvent {

        /**
         * Constructs a new World RenderEvent
         *
         * @param stack The MatrixStack used to render the element
         */
        public World(MatrixStack stack) {
            super(stack);
        }
    }

}
