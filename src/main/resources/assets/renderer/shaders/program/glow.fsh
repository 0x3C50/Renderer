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
    float totalAlpha = 0.0;
    float totalSamples = 0.0;
    float progRadius = floor(radius);
    for(float r = -progRadius; r <= progRadius; r += 1.0) {
        vec4 smple = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);

        totalAlpha = totalAlpha + smple.a;
        totalSamples = totalSamples + 1.0;

        blurred = blurred + smple;
    }
    float mappedAlpha = totalAlpha/totalSamples;
    vec3 currentColor = blurred.rgb / totalSamples;
    fragColor = vec4(mix(currentColor, texture(vanilla, texCoord).rgb, (1-mappedAlpha)*doFinal), mix(mappedAlpha, 1.f, doFinal));
}