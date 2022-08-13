#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D fbo;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float radius;


out vec4 fragColor;

void main() {
//    vec4 currentColor = texture(fbo, texCoord);
//    if (currentColor != vec4(0)) {
//        fragColor = texture(fbo, texCoord);
//        return;
//    }
    float countRgb = 0;
    float countA = 0;
    vec3 avgedRgb = vec3(0);
    float avgedA = 0;
    for(float x = -radius;x<radius;x++) {
        for(float y = -radius;y<radius;y++) {
            vec4 col = texture(fbo, texCoord+vec2(x, y)*oneTexel);
            if (col.a != 0) {
                avgedRgb += col.rgb;
                countRgb++;
            }
            avgedA += col.a;
            countA++;
        }
    }
    if (countRgb == 0) {
        fragColor = texture(DiffuseSampler, texCoord);
    } else {
        vec4 divd = vec4(avgedRgb/countRgb, avgedA/countA);
        vec4 actual = mix(texture(DiffuseSampler, texCoord), divd, divd.a*2); // take normal color
        fragColor = actual;
    }
}