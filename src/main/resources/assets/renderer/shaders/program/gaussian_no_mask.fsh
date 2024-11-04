#version 330

uniform sampler2D InSampler;

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
    float sum = 0;
    for(float j = -width; j <= width; j++) {
        sum += pdf(j, sigma);
    }
    vec4 blurred = vec4(0);
    for(float j = -width; j <= width; j+=1.0) {
        // normalize weights so they sum up to 1
        float normalizedWeight = pdf(j, sigma) / sum;
        vec2 offset = oneTexel * j * direction;
        vec4 current = texture(InSampler, texCoord + offset);
        blurred += current * normalizedWeight;
    }
    fragColor = blurred;
}