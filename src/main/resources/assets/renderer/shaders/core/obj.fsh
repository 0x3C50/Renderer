#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float LightLevel;

in vec2 texCoord0;
in float vertexDistance;
in vec4 vertexColor;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    fragColor = linear_fog(color * vec4(vec3(LightLevel),1.f), vertexDistance, FogStart, FogEnd, FogColor);
}