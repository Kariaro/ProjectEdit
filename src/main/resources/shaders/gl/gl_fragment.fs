#version 130

in vec2 pass_Uv;
in vec4 pass_Position;
in vec3 pass_Color;
in vec4 pass_ShadowCoords;

out vec4 out_Color;

uniform sampler2D dif_tex;
uniform sampler2D shadow_tex;

uniform mat4 toShadowMapSpace;
uniform bool hasShadows;
uniform bool useOnlyColors;

float calcLightFactor() {
	const float bias = 0.001;
	const float shadow = 0.2;
	
	float level = 0.0;
	for(int y = -3; y <= 3; y++) {
		for(int x = -3; x <= 3; x++) {
			float t = pass_ShadowCoords.z - texture(shadow_tex, pass_ShadowCoords.xy + vec2(x, y) / 4096.0).r;
			level += (t > bias) ? shadow:1;
		}
	}
	
	return level / 49.0;
}

void main() {
	vec4 dif;
	if(useOnlyColors) {
		dif = vec4(1, 1, 1, 1);
	} else {
		dif = texture2D(dif_tex, pass_Uv);
	}
	
	if(hasShadows) {
		float lightFactor = calcLightFactor();
		dif = vec4(dif.rgb * lightFactor, dif.a);
	}
	
	if(dif.a > 0) {
		out_Color = vec4(dif.rgb * pass_Color.rgb, dif.a);
	} else {
		discard;
	}
}
