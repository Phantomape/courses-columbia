#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

uniform	sampler2D texture_sampler;


void main()
{
    //fragColor = texture(texture_sampler, outTexCoord);
    fragColor = vec4(mvVertexNormal,1);
}