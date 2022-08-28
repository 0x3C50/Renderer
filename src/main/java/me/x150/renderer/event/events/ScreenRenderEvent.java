package me.x150.renderer.event.events;

import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenRenderEvent extends RenderEvent {

    /**
     * The rendered Screen
     */
    @Getter
    Screen screen;

    /**
     * Constructs a new event
     *
     * @param stack  The context MatrixStack
     * @param screen The screen that has been rendered
     */
    public ScreenRenderEvent(MatrixStack stack, Screen screen) {
        super(stack);
        this.screen = screen;
    }
}
