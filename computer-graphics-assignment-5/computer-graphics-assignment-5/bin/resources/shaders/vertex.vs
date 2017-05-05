#version 330 core

layout(location = 0) in vec3 position;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    gl_Position = vec4(position, 0.0, 1.0);
}