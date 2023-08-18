#version 150

#moj_import <fog.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 NormalMat;
uniform int FogShape;
uniform vec3 LightPosition;

out vec2 texCoord0;
out float vertexDistance;
out vec4 vertexColor;
out vec3 LightIntensity;
flat out vec3 FaceNormal;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    texCoord0 = UV0;
    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
    vertexColor = Color;

    vec3 WorldNormal = mat3(NormalMat) * Normal;
    vec3 lightDir = normalize(LightPosition - Position);
    FaceNormal = mat3(ModelViewMat) * Normal;

    // Wrap lighting
    float wrap = 0.5;  // Defines the "width" of the wrap effect. Higher values result in more light "wrapping around" the object.
    float intensity = dot(lightDir, WorldNormal) * 0.5 + 0.5;  // Move the range of dot product from [-1, 1] to [0, 1]
    float wrappedIntensity = clamp((intensity + wrap) / (1.0 + wrap), 0.0, 1.0);

    // Apply power function to soften edge darkness
    LightIntensity = pow(wrappedIntensity, 0.5) * vec3(1.0, 1.0, 1.0);
}
