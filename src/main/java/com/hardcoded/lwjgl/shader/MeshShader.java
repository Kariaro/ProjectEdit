package com.hardcoded.lwjgl.shader;

public class MeshShader extends ShaderObjectImpl {
	public MeshShader() {
		super(
			"/shaders/mesh/mesh_vertex.vs",
			"/shaders/mesh/mesh_fragment.fs"
		);
	}
	
	@Override
	protected void loadBinds() {
		bindAttrib(0, "in_Position");
		bindAttrib(1, "in_Uv");
		bindAttrib(2, "in_Color");
	}
}
