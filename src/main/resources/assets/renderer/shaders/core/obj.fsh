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
in vec3 LightIntensity;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }

    // Toon shading: snap LightIntensity to a discrete set of values
    vec3 toonLightIntensity = vec3(floor(LightIntensity * 5.0) / 4.0);

    // Ambient light
    vec3 ambientLight = vec3(0.3, 0.3, 0.3); // You can change this value

    // Soften the edges of the toon shading by blending it with the original light intensity
    vec3 softenedLightIntensity = mix(toonLightIntensity, LightIntensity, 0.3);

    // Add ambient light to the light intensity
    vec3 finalLightIntensity = softenedLightIntensity + ambientLight;

    vec4 shadedColor = vec4(finalLightIntensity, 1.0) * color;
    fragColor = linear_fog(shadedColor * vec4(vec3(LightLevel), 1.0), vertexDistance, FogStart, FogEnd, FogColor);
}
