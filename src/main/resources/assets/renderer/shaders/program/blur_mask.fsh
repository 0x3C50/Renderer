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
    vec4 blurred = vec4(0);
    int total = 0;
    float progRadius = max(.5f, Radius*texture(MaskSampler, texCoord).a); // min 1 sample = no blur
    for(float r = -progRadius;r<progRadius;++r) {
        blurred += texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        total += 1;
    }
    fragColor = blurred / total;
    //fragColor = texture(MaskSampler, texCoord);
}