#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D vanilla;
//uniform sampler2D fbo;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float radius;
uniform vec2 BlurDir;
uniform float doFinal;

out vec4 fragColor;

void main() {
    vec4 blurred = vec4(0.0);
    float progRadius = floor(radius);
    for (float r = -progRadius; r <= progRadius; r += 1.0) {
        vec4 smple = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        // Accumulate smoothed blur
        blurred = blurred + smple;
    }
    vec4 currentColor = blurred / (progRadius * 2.0 + 1.0);
    float mappedAlpha = currentColor.a;
    fragColor = vec4(mix(currentColor.rgb, texture(vanilla, texCoord).rgb, (1-mappedAlpha)*doFinal), mix(mappedAlpha, 1.f, doFinal));
}