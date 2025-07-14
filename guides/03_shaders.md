# Shaders

OpenGL uses core shaders for rendering, minecraft does so too but extends them a bit.

## Core shaders

Core shaders are the shaders that take vertex data and transform it into pixels on the screen. The general pipeline
looks like this:

1. Vertex data is being defined by the program, along with the related indices
2. OpenGL parses the data it's been given and invokes the vertex shader
3. The vertex shader takes each vertex and transforms them, producing output for the fragment shader (like coordinates,
   colors, UV coordinates, etc)
4. OpenGL tessellates the shape produced by the vertex shader, and invokes the fragment shader
5. The fragment shader colors each pixel with the input from the vertex shader, or `discard`s it

This isn't specific to Minecraft, and OpenGL does this everywhere. You can read more about this on
https://learnopengl.com/getting-started/shaders.

## Core shader definition

You can define a core shader via a `RenderPipeline`. The RenderPipeline defines:

1. Vertex and fragment shaders to bind
2. Samplers that should be available
3. Uniforms that should be available
4. Some state options (like depth options, polygon mode, culling, blending, etc)
5. Vertex format of the data being passed in

## Core shader usage

To use a `RenderPipeline`, create a `RenderPass` via 
`RenderSystem.getDevice().createCommandEncoder().createRenderPass(...)`.

On that RenderPass, you can:

- Set the pipeline: `setPipeline(pipeline)`
- Bind samplers: `bindSampler("SamplerName", texture)`
- Set uniforms: `setUniform("UniformGroup", buffer)`
- etc.

### About uniforms

Uniforms in this version are a bit weird, since they no longer use single, isolated uniforms, instead opting to write
data directly into the uniform groups. In your shader, you have:

```glsl
layout(std140) uniform SomeUniformGroup {
    vec2 uniform1;
    float uniform2;
    int uniform3;
// etc
};
```

This defines a uniform group "SomeUniformGroup". To then write to that uniform group, you need to obtain a `GpuBuffer`
with std140-formatted data to be written to that group. 
You can construct std140-formatted data with `Std140Builder`.
In your shader, you can then use the names "uniform1", "uniform2", ... directly, without needing to reference the
SomeUniformGroup.

## Post shaders

Post shaders are different, and are (in this form) unique to Minecraft. Post shaders are shaders, that take the output
on the framebuffer, and transform it into different output.
This is useful to apply post processing, for example the "super secret settings" or the glow outline.

Post shaders themselves are also shaders and are written in glsl, but they're formatted slightly differently and are
defined differently.
In general, the only post shader you should need is a fragment shader, since the post effect is done by rendering a full
screen textured quad over the framebuffer. Modifying the vertex data here would be quite useless.

The individual fragment shaders are part of a "pipeline", that is drawn each time you draw the post effect. You can have
multiple shader invocations in the same pipeline, with different settings. An example pipeline with 2 invocations of a
shader called `example:postshader` would look like this:

```json5
{
	// which additional textures to allocate for the shader
	"targets": {
		// "give me an extra target called 'swap', with the default settings (same with and height as main texture, black by default, "persistent")"
		"swap": {}
	},
	// which passes to draw as part of this pipeline
	"passes": [
		{
			// the vertex shader to use (sobel is identity, and defines some useful information. see sobel.vsh)
			"vertex_shader": "minecraft:post/sobel",
			// the fragment shader to use. this is our shader (at assets/example/shaders/postshader.fsh)
			"fragment_shader": "example:postshader",
			// texture inputs to give to our shader. either a framebuffer by id or a texture
			"inputs": [
				{
					// "give me the framebuffer called minecraft:main, with bilinear filtering, into the InSampler uniform"
					"sampler_name": "In",
					"target": "minecraft:main",
					"bilinear": true
				},
				// "give me the 128x128 texture at example:texture into TextureSampler, with bilinear filtering"
				{
					"sampler_name": "Texture",
					"location": "example:texture",
					"width": 128,
					"height": 128,
					"bilinear": false
				}
			],
			// write the output of this shader pass into our swap framebuffer
			"output": "swap",
			// other uniforms in their groups. see above for explanation of groups themselves
			// available types (name here and in glsl is the same except for mat4):
			//    int, ivec3, float, vec2, vec3, vec4, matrix4x4 -> (mat4)
			"uniforms": {
				// the named uniform group
				"Predefined": [
					// all values in that uniform group, unnamed. beware of order! these will be written in this order to the buffer!
					{
						// type of the uniform
						"type": "vec2",
						// value of the uniform, depends on type
						// for vectors: array with length = n components
						// for scalars: single integer or float
						// for mat4: array with 16 elements
						"value": [
							1,
							0
						],
					}
				],
				"BlurConfig": [
					// named uniform group with 2 elements
					{
						"value": 0,
						"type": "float"
					},
					{
						"value": 0,
						"type": "float"
					}
				]
			}
		},
		// second pass
		{
			"vertex_shader": "minecraft:post/sobel",
			"fragment_shader": "renderer:program/gaussian",
			"inputs": [
				{
					"sampler_name": "In",
					// this time, input from swap framebuffer
					"target": "swap",
					"bilinear": true
				},
				{
					"sampler_name": "Texture",
					"location": "example:texture",
					"width": 128,
					"height": 128,
					"bilinear": false
				}
			],
			// and output to main
			"output": "minecraft:main",
			"uniforms": {
				"Predefined": [
					{
						"value": [
							0,
							1
						],
						"type": "vec2"
					}
				],
				"BlurConfig": [
					{
						"value": 0,
						"type": "float"
					},
					{
						"value": 0,
						"type": "float"
					}
				]
			}
		}
	]
}
```

You can load a post pipeline with:
`MinecraftClient#getShaderLoader().loadPostEffect(shaderLocation, setOfAvailableTargets)`

The `setOfAvailableTargets` lists external targets that are available to this shader. This includes all targets that are
not defined in the shader itself. If you use `minecraft:main` as input or output of your shader, and dont define a
`minecraft:main` target in the targets list, `minecraft:main` would then become an external target. You need to be able
to supply that target from the outside when drawing the shader.