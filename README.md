# Renderer
An easy to use rendering library for modern fabricmc

# Installing
You can install this library using [Jitpack](https://jitpack.io/)

To install the library, just add the jitpack repository to your repositories block as described on the website, and this to your dependencies block in build.gradle:

```groovy
include modImplementation("com.github.0x3C50:Renderer:master-SNAPSHOT")
```
This will always update with the latest commit, do not worry about being out of date.

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
    @EventListener(shift= Shift.POST, type = EventType.HUD_RENDER)
    void preHudRender(RenderEvent re) {
        MSAAFramebuffer.use(MSAAFramebuffer.MAX_SAMPLES, () -> {
            ClipStack.globalInstance.addWindow(re.getStack(),new Rectangle(20,10,110,110));
            Renderer2d.renderRoundedQuad(re.getStack(), Color.WHITE,10,10,100,100,5,20);
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
RenderAction.draw() will draw the action, **but will NOT clear the VBO after it's done**. .draw() is being used to indicate that you want to use this VBO afterwards for faster performance on **large renders**.

.drawOnce() will do exactly what it says, it draws the VBO **once** and deletes it after. So if you're drawing conventionally, use .drawOnce()! You can also regenerate the VBO after it's rendered when you use .drawOnce() multiple times, but that is not recommended. Draw once, forget about it.

Example: `Renderer3d.something().drawOnce(matrixStack);`

If you do want to reuse the VBO afterwards, save the RenderAction somewhere in a variable and reuse the .draw() method. If you're done, don't forget to call action.delete()

# Event shifts
PRE is for when you want to monitor an event happening and maybe cancel it, POST is for when you want to additionally render stuff when it happens.

# Suggestions
You can leave suggestions in the issues section