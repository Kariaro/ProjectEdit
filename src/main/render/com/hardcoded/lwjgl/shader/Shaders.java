package com.hardcoded.lwjgl.shader;

public final class Shaders {
	private static ShadowShader shadow_shader;
	private static MeshShader mesh_shader;
	
	private static Shader position_shader;
	private static Shader position_color_shader;
	private static Shader position_uv_shader;
	private static Shader position_color_uv_shader;
	
	/**
	 * Only call this on the GL thread
	 */
	public static void init() {
		shadow_shader = new ShadowShader();
		mesh_shader = new MeshShader();
		
		
		position_shader = new UniversalShader(0);
		position_uv_shader = new UniversalShader(UniversalShader.HAS_TEXTURE);
		position_color_shader = new UniversalShader(UniversalShader.HAS_COLOR);
		position_color_uv_shader = new UniversalShader(UniversalShader.HAS_TEXTURE | UniversalShader.HAS_COLOR);
	}
	
	public static final ShadowShader getShadowShader() {
		return shadow_shader;
	}
	
	public static final MeshShader getMeshShader() {
		return mesh_shader;
	}
	
	
	// Universal shaders
	
	public static final Shader getPositionShader() {
		return position_shader;
	}
	
	public static final Shader getPositionColorShader() {
		return position_color_shader;
	}
	
	public static final Shader getPositionUvShader() {
		return position_uv_shader;
	}
	
	public static final Shader getPositionColorUvShader() {
		return position_color_uv_shader;
	}
}
