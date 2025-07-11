package me.x150.renderer.util;

import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.render.RenderLayer;

import java.util.function.Consumer;

/**
 * A duck interface for {@link RenderLayer.MultiPhase}, allowing to specify a custom action to apply to the {@link RenderPass} before drawing.
 */
public interface MoreMultiPhase {
	/**
	 * Specifies an action to apply to a {@link RenderPass} before drawing happens.
	 * @param rp Action to apply
	 * @return this
	 */
	RenderLayer.MultiPhase withRenderPassSetup(Consumer<RenderPass> rp);

	/**
	 * Helper function to hide the ugly cast since {@link RenderLayer.MultiPhase} is final
	 * @param mp MultiPhase to convert
	 * @return MoreMultiPhase for the given MultiPhase
	 */
	static MoreMultiPhase moreOptions(RenderLayer.MultiPhase mp) {
		return ((MoreMultiPhase) (Object) mp);
	}
}
