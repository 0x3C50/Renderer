# Renderer
An easy to use rendering library for modern fabric

## WARNING
This library is still in beta, and might break or behave funny. If you encounter any unwanted behaviour, please open an issue.

# Installing
You can install this library using [Jitpack](https://jitpack.io/)

To install the library, just add the jitpack repository to your repositories block as described on the website, and this to your dependencies block in build.gradle:

```groovy
include modImplementation("com.github.0x3C50:Renderer:rewrite-SNAPSHOT")
```
This will use the latest commit as build target, but will cache that build target every time. Use the latest short commit hash found on [Jitpack](https://jitpack.io/#0x3C50/Renderer) (example: `d2cc995ff4`) as the version, to get that release instead.

# Usage
The api has an extensive javadoc, which explains almost anything. The wiki also has some more insights and examples.

# The two renderers
Renderer2d draws in 2 dimensions, on the hud. Renderer3d draws in 3 dimensions, in the world.

# Events
This library has its own event system, which broadcasts some basic rendering events (hud render, world render, etc). You can find more information on the wiki (soon:tm:)

# Suggestions
You can leave suggestions in the issues section

If this library helped you, please consider leaving a star, since this library took me a while to make as it is right now :)

# Examples
More examples can be found in `RendererClient`.

```java
class Listener {
    void main() {
        Events.manager.registerSubscribers(this);
    }
    @MessageSubscription
    void onWorldRendered(RenderEvent.World world) {
        // Quad at (0, 0, 0):
        Renderer3d.renderFilled(world.getMatrixStack(), Color.RED, Vec3d.ZERO, new Vec3d(1, 1, 1));
        // Quad outline at (0, 0, 0):
        Renderer3d.renderOutline(world.getMatrixStack(), Color.RED, Vec3d.ZERO, new Vec3d(1, 1, 1));
    }
    
    @MessageSubscription
    void onHudRendered(RenderEvent.Hud hud) {
        // Rounded quad at (50, 50 -> 100, 100), 5 px corner, 10 samples
        Renderer2d.renderRoundedQuad(RendererUtil.getEmptyMatrixStack(), Color.WHITE, 50, 50, 100, 100, 5, 10);
    }
}
```