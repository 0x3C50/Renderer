package me.x150.renderer.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Something has been rendered
 */
@Getter
public class RenderEvent extends Event {
    /**
     * The MatrixStack used to render the element
     */
    MatrixStack matrixStack;

    /**
     * Constructs a new RenderEvent
     * @param stack The MatrixStack used to render the element
     * @param shift The shift
     */
    public RenderEvent(MatrixStack stack, Shift shift) {
        super(shift);
        this.matrixStack = stack;
    }

    /**
     * The hud has been rendered
     */
    public static class Hud extends RenderEvent {
        /**
         * Constructs a new HUD RenderEvent
         * @param matrixStack The MatrixStack used to render the element
         * @param shift The shift
         */
        public Hud(MatrixStack matrixStack, Shift shift) {
            super(matrixStack, shift);
        }
    }

    public static class World extends RenderEvent {
        /**
         * Constructs a new world RenderEvent
         * @param matrixStack The MatrixStack used to render the element
         * @param shift The shift
         */
        public World(MatrixStack matrixStack, Shift shift) {
            super(matrixStack, shift);
        }
    }

}
