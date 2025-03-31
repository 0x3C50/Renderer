package me.x150.renderer.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Events, that trigger at a specific point in the render pipeline.
 */
public class RenderEvents {
	/**
	 * Triggers just AFTER the world is rendered, before the hand is rendered.
	 * This is after {@link net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents#END}, but at the same logical position in the rendering pipeline.
	 * The given MatrixStack contains the inverse camera rotation.
	 */
	public static final Event<RenderEvent<MatrixStack>> AFTER_WORLD = create();

	/**
	 * Triggers just AFTER the hud is rendered
	 */
	public static final Event<RenderEvent<DrawContext>> HUD = create();

	private static <T> Event<RenderEvent<T>> create() {
		return EventFactory.createArrayBacked(RenderEvent.class, listeners -> element -> {
			for (RenderEvent<T> listener : listeners) {
				listener.rendered(element);
			}
		});
	}

	@FunctionalInterface
	public interface RenderEvent<T> {
		void rendered(T context);
	}
}
