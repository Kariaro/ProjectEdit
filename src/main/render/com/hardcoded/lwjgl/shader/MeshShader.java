package com.hardcoded.lwjgl.shader;

import org.lwjgl.opengl.GL20;

/**
 * A mesh shader
 * 
 * @author HardCoded
 */
public class MeshShader extends ShaderObjectImpl {
	protected int load_hasShadows;
	protected int load_useOnlyColors;
	
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
	
	@Override
	protected void loadUniforms() {
		super.loadUniforms();
		
		load_hasShadows = getUniformLocation("hasShadows");
		load_useOnlyColors = getUniformLocation("useOnlyColors");
		setUseShadows(true);
	}
	
	public void setUseShadows(boolean enable) {
		GL20.glUniform1i(load_hasShadows, enable ? 1:0);
	}
	
	public void setUseOnlyColors(boolean enable) {
		GL20.glUniform1i(load_useOnlyColors, enable ? 1:0);
	}
}
