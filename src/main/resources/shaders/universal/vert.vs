#version 460 core

#define HAS_TEXTURE
#define HAS_COLOR

in vec3 in_Position;

#ifdef HAS_TEXTURE
in vec2 in_Uv;
out vec2 pass_Uv;
#endif

#ifdef HAS_COLOR
in vec4 in_Color;
out vec4 pass_Color;
#endif

uniform mat4 projectionMatrix;
uniform mat4 translationMatrix;

void main() {
	vec4 position = projectionMatrix * translationMatrix * vec4(in_Position, 1);
	gl_Position = position;
	
#ifdef HAS_TEXTURE
	pass_Uv = in_Uv;
#endif

#ifdef HAS_COLOR
	pass_Color = in_Color;
#endif
}
