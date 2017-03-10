#version 330

in vec4 lightColor;
flat in int dummy;

out vec4 fragColor;
out int dummy2;

void main()
{
    dummy2 = dummy;
    fragColor = lightColor;
}