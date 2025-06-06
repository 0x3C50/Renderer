#version 150

#moj_import <minecraft:dynamictransforms.glsl>

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 distanceToCenter = texCoord0 - vec2(0.5, 0.5);
    float distance = length(distanceToCenter) * 2;
    distance = smoothstep(1.0 - fwidth(distance), 1.0, distance);
    float alpha = 1-distance;
//    distance *= 2;
    vec4 color = ColorModulator * vec4(1, 1, 1, alpha);
    if (color.a == 0) discard;
    fragColor = color;
}
