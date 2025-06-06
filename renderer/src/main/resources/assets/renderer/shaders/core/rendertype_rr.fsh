#version 150

#moj_import <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec2 widthHeight;
in vec4 roundness;
in vec4 vertexColor;

out vec4 fragColor;

float sdRoundBox( vec2 p, vec2 b, vec4 r )
{
    r.xy = (p.x>0.0)?r.xy : r.zw;
    r.x  = (p.y>0.0)?r.x  : r.y;
    vec2 q = abs(p)-b+r.x;
    return min(max(q.x,q.y),0.0) + length(max(q,0.0)) - r.x;
}


void main() {
    float distance = sdRoundBox(texCoord0 - widthHeight / 2, widthHeight / 2, roundness);
    float fw = fwidth(distance);
    distance = smoothstep(0, fw, distance);
    float alpha = 1-distance;
    vec4 color = ColorModulator * vertexColor * vec4(1, 1, 1, alpha);
    if (color.a == 0) discard;
    fragColor = color;
}
