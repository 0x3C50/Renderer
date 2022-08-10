# Renderer
An easy to use rendering library for modern fabricmc

# Installing
You can install this library using [Jitpack](https://jitpack.io/)

To install the library, just add the jitpack repository to your repositories block as described on the website, and this to your dependencies block in build.gradle:

```groovy
include modImplementation("com.github.0x3C50:Renderer:master-SNAPSHOT")
```
This will use the latest commit as build target, but will cache that build target every time. Use the latest short commit hash found on [Jitpack](https://jitpack.io/#0x3C50/Renderer) (example: `d2cc995ff4`) as the version, to get that release instead.

# Usage
Using this library is very simple, here's an example:

## Mod initializer class
```java
public class RendererTestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Events.registerEventHandlerClass(new EventHandler());
    }
}

```

## EventHandler.java
```java
public class EventHandler {
    @EventListener(shift = Shift.POST, type = EventType.HUD_RENDER)
    void preHudRender(RenderEvent re) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            ClipStack.globalInstance.addWindow(re.getStack(), new Rectangle(20,10,110,110));
            Renderer2d.renderRoundedQuad(re.getStack(), Color.WHITE, 10, 10, 100, 100, 5, 20);
            ClipStack.globalInstance.popWindow();
        });
    }
}
```

This will render an anti aliased rounded rectangle with 5px of corners and 20 samples with the left side cut off top left of the screen, each time the hud is rendered

The api is pretty self explanatory, you just register an event for which you want to listen and render your stuff in there.

# The two renderers
Renderer2d is for 2d rendering on screens or the hud, Renderer3d is to be used inside the WORLD_RENDER event, that's where it thrives.

# World rendering
Rendering inside the world is a bit different than rendering on the screen. The world renderer (Renderer3d) uses VBOs to cache whatever it's doing, and will return a RenderAction instead of drawing it itself.

## Caution with RenderAction
A RenderAction can either be
- rendered once and forgotten about, or
- reused multiple times

To reuse a RenderAction, save the RenderAction in a variable and call `.drawWithVBO()` on it each time you want to render it. After you're done with it, **do not forget to call `.delete()`**. This option works best when you have a big buffer and need to render it multiple times.

To draw a RenderAction, call `.drawWithoutVBO()`. This will use a throwaway, reusable VBO. This leads to faster performance while rendering once. If you need to reuse the VBO, the option above will yield better results. You do not have to call `.delete()` when using this.

# Event shifts
PRE is for when you want to monitor an event happening and maybe cancel it, POST is for when you want to additionally render stuff when it happens.

# Suggestions
You can leave suggestions in the issues section