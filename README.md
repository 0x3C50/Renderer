# Renderer
An easy to use rendering library for modern fabricmc

# WARNING
This library uses some very old rendering techniques, and is barely maintained anymore. It does still work, but there's no guarantee it will continue to do so in a few updates. A remake will eventually be public.

You can still use this library, and it will probably work, but I do not know for how long.

# Installing
You can install this library using [Jitpack](https://jitpack.io/)

To install the library, just add the jitpack repository to your repositories block as described on the website, and this to your dependencies block in build.gradle:

```groovy
include modImplementation("com.github.0x3C50:Renderer:master-SNAPSHOT")
```
This will use the latest commit as build target, but will cache that build target every time. Use the latest short commit hash found on [Jitpack](https://jitpack.io/#0x3C50/Renderer) (example: `d2cc995ff4`) as the version, to get that release instead.

# Usage
The api has an extensive javadoc, which explains almost anything. The wiki also has some more insights and examples.

# The two renderers
Renderer2d draws in 2 dimensions, on the hud. Renderer3d draws in 3 dimensions, in the world or with a fake 3d context.

# Faking 3d rendering
This library allows you to fake a 3d scene with the CameraContext3D class, which can be used to render 3d content on the hud. More on that in the wiki.

# World rendering
Rendering inside the world is a bit different than rendering on the screen. The world renderer (Renderer3d) uses VBOs to cache whatever it's doing, and will return a RenderAction instead of drawing it itself.

## Caution with RenderAction
A RenderAction can either be
- rendered once and forgotten about, or
- reused multiple times

To reuse a RenderAction, save the RenderAction in a variable and call `.drawWithVBO()` on it each time you want to render it. After you're done with it, **do not forget to call `.delete()`**. This option works best when you have a big buffer and need to render it multiple times.

To draw a RenderAction, call `.drawWithoutVBO()`. This will use a throwaway, reusable VBO. This leads to faster performance while rendering once. If you need to reuse the VBO, the option above will yield better results. You do not have to call `.delete()` when using this.

## Custom RenderActions
If you have a large buffer you want to render multiple times, upload it to a RenderAction and it will manage everything for you.

# Events
This library allows you to monitor specific rendering events, an example can be found within the EventHandler class, which is used to render the fading blocks. The wiki also contains examples and explanations.

## Event shifts
PRE is for when you want to monitor an event happening and maybe cancel it, POST is for when you want to additionally render stuff when it happens.

# Suggestions
You can leave suggestions in the issues section

If this library helped you, please consider leaving a star, since this library took me a while to make as it is right now :)