package com.hardcoded.lwjgl.shader;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

/**
 * A mesh shader
 * 
 * @author HardCoded
 */
public class MeshShader extends ShaderObjectImpl {
	protected int load_toShadowMapSpace;
	protected int load_projectionView;
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
		
		load_toShadowMapSpace = getUniformLocation("toShadowMapSpace");
		load_projectionView = getUniformLocation("projectionView");
		load_hasShadows = getUniformLocation("hasShadows");
		load_useOnlyColors = getUniformLocation("useOnlyColors");
		
		setUniform("dif_tex", 0);
		setUniform("shadow_tex", 1);
		setUseShadows(true);
	}
	
	public void setProjectionView(Matrix4f projectionView) {
		setMatrix4f(load_projectionView, projectionView);
	}
	
	public void setShadowMapSpace(Matrix4f matrix) {
		setMatrix4f(load_toShadowMapSpace, matrix);
	}
	
	public void setUseShadows(boolean enable) {
		GL20.glUniform1i(load_hasShadows, enable ? 1:0);
	}
	
	public void setUseOnlyColors(boolean enable) {
		GL20.glUniform1i(load_useOnlyColors, enable ? 1:0);
	}
}
