#version 460 core

in vec4 in_Position;
in vec3 in_Normal;
in vec2 in_Uv;
in vec4 in_Color;

uniform mat4 matrix;

void main() {
	gl_Position = matrix * in_Position;
}
