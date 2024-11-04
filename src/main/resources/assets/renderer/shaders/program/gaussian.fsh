#version 330

uniform sampler2D InSampler;
uniform sampler2D MaskSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform float width;
uniform float sigma;
uniform vec2 direction;

out vec4 fragColor;

float pdf(float x, float sigma) {
    return exp(-((x*x) / (sigma*sigma)));
}

void main() {
//    float weights[width*2+1];
    float radiusMul = texture(MaskSampler, texCoord).a;
    if (radiusMul == 0) {
        fragColor = texture(InSampler, texCoord);
        return;
    }
    float fullRadius = (width * radiusMul);
    float sum = 0;
    for(float j = -fullRadius; j <= fullRadius; j++) {
        sum += pdf(j, sigma);
    }
    vec4 blurred = vec4(0);
    for(float j = -fullRadius; j <= fullRadius; j+=1.0) {
        // normalize weights so they sum up to 1
        float normalizedWeight = pdf(j, sigma) / sum;
        vec2 offset = oneTexel * j * direction;
        vec4 current = texture(InSampler, texCoord + offset);
        blurred += current * normalizedWeight;
    }
    fragColor = blurred;
}