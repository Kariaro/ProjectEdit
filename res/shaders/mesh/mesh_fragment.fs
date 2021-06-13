#version 130

in vec2 pass_Uv;
in vec4 pass_Color;
in vec4 pass_ShadowCoords;

out vec4 out_Color;

uniform sampler2D dif_tex;
uniform sampler2D shadow_tex;

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
	vec4 dif = texture2D(dif_tex, pass_Uv);
	float lightFactor = calcLightFactor();
	dif = vec4(dif.rgb * lightFactor, dif.a);
	
	vec3 col_a = dif.rgb * (1 - pass_Color.a);
	vec3 col_b = pass_Color.rga * pass_Color.a;
	out_Color = vec4(col_a + col_b, dif.a * (1 - pass_Color.a) + pass_Color.a);
}
