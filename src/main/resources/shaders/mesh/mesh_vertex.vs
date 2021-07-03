#version 130

in vec4 in_Position;
in vec2 in_Uv;
in vec3 in_Color;

out vec2 pass_Uv;
out vec4 pass_Position;
out vec3 pass_Color;
out vec4 pass_ShadowCoords;

uniform mat4 toShadowMapSpace;
uniform mat4 projectionView;
uniform mat4 translationMatrix;

void main() {
	vec4 position = translationMatrix * in_Position;
	gl_Position = projectionView * position;
	pass_ShadowCoords = toShadowMapSpace * position;
	pass_Position = position;
	pass_Uv = in_Uv;
	pass_Color = in_Color;
}
