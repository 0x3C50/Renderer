# Migration to renderer 2.0
## What happened to...
- `me.x150.renderer.font`? Removed; reimplemented properly with `.fontng`
- `me.x150.renderer.objfile`? Removed, to be reimplemented later (maybe)
- `me.x150.renderer.render.MSAAFramebuffer`? Removed, not really doable with the new system. Not needed with this library anymore anyway
- `me.x150.renderer.render.MaskedBlurFramebuffer`? Reimplemented with `ShaderManager.drawBlur`, tho slightly neutered because of the new system
- `me.x150.renderer.render.OutlineFramebuffer`? Removed as per deprecation
- `.Renderer2d`? Partly reimplemented in `ExtendedDrawContext`. The rest can be found in vanilla `DrawContext`
- `.Renderer3d`? Mostly reimplemented in `WorldRenderContext`.
- `.SVGFile`? Removed, to be reimplemented later (maybe)
- `.util.BufferUtils`? Removed, not needed anymore
- `.Colors`? Removed, use vanilla `ColorHelper`
- `.FastMStack`? Removed, use vanilla `MatrixStack`
- `.RendererUtils`? Renamed to `.RenderUtils`

Some things have been moved around or renamed.

## Design changes
Most of the rendering has been moved over to render layers. The render layers specific to this library can be found in `CustomRenderLayers`.