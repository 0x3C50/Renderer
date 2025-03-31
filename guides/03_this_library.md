# This library
Renderer aims to provide some common utilities for more advanced rendering in Minecraft. For example:
- Font shaping and rendering
- More Render layers
- Common primitive shapes to draw in the world and on the hud
- Utilities for rendering in general

Most of the library's design is similar to the rendering design from Minecraft. That means, most of the rendering is done via Render layers.

## Hud Rendering
### Fonts
Font rendering uses HarfBuzz and Freetype. GlyphBuffers allow you to collect shaped runs into one glyph "list", and draw all of them at once.
To draw a GlyphBuffer, give it a VertexConsumerProvider and a position to draw at.

### Hud Rendering
Hud rendering is implemented as an extension to DrawContext. DrawContext is a vanilla class that implements some basic hud rendering.
`ExtendedDrawContext` takes a DrawContext instance to do some more operations, like drawing lines and rounded rectangles.

### World Rendering
World rendering is slightly more complicated, since there are no existing vanilla systems to go off of.
You can create a new instance of `WorldRenderContext` to hold the current world rendering state (such as camera position),
then use that to draw things in the world in the given RenderLayers. Which RenderLayers are appropriate are described in the javadoc of the
methods.

### Utilities
Just look around the library! Something will probably be useful to you.