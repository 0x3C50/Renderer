#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D MaskSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;

out vec4 fragColor;

void main() {
    float progRadius = Radius*texture(MaskSampler, texCoord).a; // multiply our max radius by the alpha of the current pixel
    if (progRadius < 1) {
        fragColor = texture(DiffuseSampler, texCoord); // no change, sample size less than one pixel
    } else {
        vec4 blurred = vec4(0);
        int i = 0;
        for(float r = -progRadius;r<=progRadius;r++) { // ex progRadius 2: -2, -1, 0, 1, 2
            blurred += texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
            i++;
        }
        fragColor = blurred / i;
    }
}