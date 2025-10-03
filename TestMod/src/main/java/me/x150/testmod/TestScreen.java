package me.x150.testmod;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import me.x150.testmod.tests.FontRendererTest;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class TestScreen extends BaseOwoScreen<FlowLayout> {
	private record Entry(String name, Component comp) {}
	private static final Entry[] entries = {
		new Entry("Font renderer", new FontRendererTest().sizing(Sizing.fill())),
		new Entry("test2", Components.label(Text.literal("real2"))),
	};
 	@Override
	protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
		return OwoUIAdapter.create(this, Containers::horizontalFlow);
	}

	@Override
	protected void build(FlowLayout flowLayout) {
		flowLayout
				.gap(10)
				.surface(Surface.flat(0xFF000000))
				.sizing(Sizing.fill(), Sizing.fill())
		;
		FlowLayout selector = Containers.verticalFlow(Sizing.content(), Sizing.content()).gap(5);
		for (Entry entry : entries) {
			selector.child(Components.button(Text.literal(entry.name), buttonComponent -> {
				Component element = flowLayout.childById(Component.class, "element");
				if (element != null) element.remove();
				flowLayout.child(entry.comp.id("element"));
			}));
		}
		flowLayout.child(Containers.verticalScroll(Sizing.content(), Sizing.fill(), selector));
	}
}
