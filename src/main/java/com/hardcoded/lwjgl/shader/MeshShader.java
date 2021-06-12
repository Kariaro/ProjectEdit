package com.hardcoded.lwjgl.shader;

public class MeshShader extends ShaderObjectImpl {
//	protected int load_translation;
	
	public MeshShader() {
		super(
			"/shaders/mesh/mesh_vertex.vs",
			"/shaders/mesh/mesh_fragment.fs"
		);
	}
	
	@Override
	protected void loadBinds() {
//		load_translation = getUniformLocation("translation");
		
		bindAttrib(0, "in_Position");
		bindAttrib(1, "in_Uv");
	}

//	public void setTranslation(float x, float y, float z) {
//		GL20.glUniform3f(load_translation, x, y, z);
//	}
}
