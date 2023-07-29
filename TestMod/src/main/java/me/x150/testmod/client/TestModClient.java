package me.x150.testmod.client;


import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.objfile.ObjFile;
import me.x150.testmod.Handler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Optional;

public class TestModClient implements ClientModInitializer {
	/**
	 * Runs the mod initializer on the client environment.
	 */
	@Override
	public void onInitializeClient() {
		RenderEvents.HUD.register(Handler::hud);
		RenderEvents.WORLD.register(Handler::world);

		defineResourceListener();
	}

	public static ObjFile testObj;
	private void defineResourceListener()
	{
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier("testmod", "resource_listener");
			}

			@Override
			public void reload(ResourceManager manager) {
				Identifier basePath = new Identifier("testmod", "obj");

				ObjFile.ResourceProvider provider = TestLocalResourceProvider.ofResourceManager(manager, basePath);
				try {
					testObj = new ObjFile("testsphere.obj", (ObjFile.ResourceProvider) provider);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public interface TestLocalResourceProvider extends ObjFile.ResourceProvider {
		static TestLocalResourceProvider ofResourceManager(ResourceManager resourceManager, Identifier basePath) {
			return name -> {
				Identifier resourceIdentifier = new Identifier(basePath.getNamespace(), basePath.getPath() + "/" + name);
				Optional<Resource> resource;
				resource = resourceManager.getResource(resourceIdentifier);
				if (resource.isPresent()) {
					return resource.get().getInputStream();
				} else {
					throw new IOException("Resource not found: " + resourceIdentifier.toString());
				}
			};
		}
	}
}
