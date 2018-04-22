#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;
out int dummyUseColour;
out float dummyReflectance;
out int dummy;

struct Material
{
    vec3 colour;
    int useColour;
    int hasNormalMap;
    float reflectance;
};

uniform vec3 ambientLight;
uniform Material material;

void main()
{
    vec4 baseColour = vec4(material.colour, 1.0);
    vec4 redTint = vec4(0.5, 0.0, 0.0, 1.0);
    vec4 totalLight = vec4(ambientLight, 1.0);

    dummyReflectance = material.reflectance * 10;
    dummyUseColour = material.useColour * 10;
    dummy = material.hasNormalMap;
    fragColor = (baseColour + redTint) * totalLight;
}