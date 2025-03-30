/**
 * Font rendering utilities, using HarfBuzz and Freetype.
 * Example:
 * <pre><code>
 * // Once, to initialize:
 * FTLibrary library = new FTLibrary();
 * Font mainFont = new Font(library, "SomeFont.ttf", 0, 20);
 * FontScalingRegistry.register(mainFont);
 *
 * // To shape new text (shouldn't be done every frame if possible, this is expensive!)
 * GlyphBuffer buffer = new GlyphBuffer();
 * buffer.addString(mainFont, "Some text: ", 0, 0)
 * 		.then(mainFont, Text.literal("some styled text! ").styled(it -> it.withColor(0xFF0000).withUnderline(true)), 0, 0)
 * 		.then(mainFont, "and some more regular text", 0, 0);
 * buffer.offsetToTopLeft(); // ensure buffer is viewed from top left coordinate
 *
 * // to draw text:
 * VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(new BufferAllocator(1024)); // or some other source
 * MatrixStack ms = RendererUtils.getEmptyMatrixStack();
 * buffer.draw(vcp, ms, 100, 100);
 * vcp.draw();
 *
 * // cleanup:
 * mainFont.close();
 * library.close();
 * </code></pre>
 */
package me.x150.renderer.fontng;