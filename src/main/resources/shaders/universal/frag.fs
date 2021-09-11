#version 460 core

#define HAS_TEXTURE
#define HAS_COLOR

#ifdef HAS_TEXTURE
in vec2 pass_Uv;
#endif

#ifdef HAS_COLOR
in vec4 pass_Color;
#endif


#ifdef HAS_TEXTURE
uniform sampler2D dif_tex;
#endif

uniform vec4 uniform_Color;

out vec4 FragColor;

void main() {
	vec4 dif;
	
#ifdef HAS_TEXTURE
    dif = texture(dif_tex, pass_Uv) * uniform_Color;
#else
	dif = uniform_Color;
#endif

#ifdef HAS_COLOR
	dif *= pass_Color;
#endif
	
	if(dif.a > 0) {
		FragColor = vec4(dif.rgb, dif.a);
	} else {
		discard;
	}
}
