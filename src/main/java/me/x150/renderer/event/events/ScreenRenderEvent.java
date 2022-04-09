package me.x150.renderer.event.events;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenRenderEvent extends RenderEvent {
    Screen screen;

    public ScreenRenderEvent(MatrixStack stack, Screen screen) {
        super(stack);
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}
