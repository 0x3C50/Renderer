#version 330

uniform sampler2D InSampler;
uniform sampler2D MaskSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float Radius;
uniform vec4 OutlineColor;
uniform vec4 InnerColor;

out vec4 fragColor;

void main() {
    vec4 current = texture(MaskSampler, texCoord);
    vec4 currentNormal = texture(InSampler, texCoord);

    if (current.a != 0) {
        // mask pixel set
        fragColor = vec4(mix(currentNormal.rgb, InnerColor.rgb, InnerColor.a), 1);
        return;
    }

    bool seenSelect = false;
    bool seenNonSelect = false;
    for(float x = -Radius; x <= Radius; x++) {
        for(float y = -Radius; y <= Radius; y++) {
            vec2 offset = vec2(x, y);
            vec2 coord = texCoord + offset * oneTexel;
            vec4 t = texture(MaskSampler, coord);
            if (t.a == 1) seenSelect = true;
            else if (t.a == 0) seenNonSelect = true;
        }
    }
    if (seenSelect && seenNonSelect) fragColor = vec4(mix(currentNormal.rgb, OutlineColor.rgb, OutlineColor.a), 1);
    else fragColor = currentNormal;
}