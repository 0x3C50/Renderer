# Design of the Minecraft rendering system
The Minecraft rendering system, at its core, is comprised of RenderPhases and a rendering device, that manages the resources for rendering.
A RenderPhase defines one "type" of render, that being things like the vertex format and layout, which shaders to use, which resources to
allocate for that shader, etc. You can draw things to the screen using only RenderPhases.

To do this, you have to:
1. Make a Buffer with the data you want to draw
2. Make a new render pass using the device manager
3. Setup the pass, setting the phase, setting uniforms, etc
4. Draw the created Buffer with the related indices

RenderLayers are an extension on top of the barebones system, which allow for buffer reuse among other things. A RenderLayer specifies
some more settings for a "type" of rendering, such as depth test state, blending state, used resources, etc. You can request a BufferBuilder
for a given RenderLayer from a VertexConsumerProvider.

Some VertexConsumerProviders (such as Immediate) can reuse existing Buffers, to save draw calls. If you request a Buffer for the same RenderLayer
multiple times, these implementations may return the same Buffer every time. This saves some time when requesting drawing.

A common footgun already: The VertexConsumerProvider isn't required to draw the buffers after the frame ends. It's wise to call `.draw()` on Immediate
after you're completely done using it, and are managing the VCP.

## From vertices to screen pixels
1. You get a BufferBuilder from a VertexConsumerProvider with your RenderLayer
2. You add the vertices and related information to the BufferBuilder
3. You call `VCP.draw()`
4. The VCP calls `RenderLayer.draw(buffer)` on the now constructed buffer with its vertices
5. The RenderLayer sets up a RenderPass
6. The RenderLayer draws your constructed buffer with a dummy index buffer using the state it's been given
7. The RenderPass binds all related resources, sets up shader uniforms, etc
8. The RenderPass calls upon OpenGL to draw the given buffer with the current state
9. OpenGL runs its normal pipeline through, running the data through the vertex and fragment shader, until it lands in the target given by the RenderLayer

## Specifying where to draw to
Framebuffers are collections of color and depth textures, mimicing the OpenGL structure. They aren't a 1-1 mapping tho; they don't have their own OpenGL id.

Instead, the textures are 1-1 mappings to OpenGL textures, and the color texture can be used to make a new OpenGL FBO with a given depth texture.

You can specify a target with RenderLayers, specifying a Framebuffer. For more low level control, the barebones system allows you to pass in
both a color texture and depth texture, along with optional clear values, when you make a new pass. The data will be drawn to these textures.

Modifying where to draw to on the fly is **not possible anymore** (without modifications). RenderLayers use their target texture when making the render pass,
and other pass construction occurrences use MinecraftClient.getFramebuffer() as the target.