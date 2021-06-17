package com.hardcoded.lwjgl.shader;

import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * This abstract class adds light and transformation access to shaders
 * 
 * @author HardCoded
 */
public abstract class ShaderObjectImpl extends Shader {
	protected static final Vector3f DEFAULT_EMPTY = new Vector3f();
	
	protected int load_toShadowMapSpace;
	protected int load_projectionView;
	
	protected ShaderObjectImpl(String vertex, String fragment) {
		super(vertex, fragment, Map.of());
	}
	
	@Override
	protected void loadUniforms() {
		load_toShadowMapSpace = getUniformLocation("toShadowMapSpace");
		load_projectionView = getUniformLocation("projectionView");

		setUniform("dif_tex", 0);
		setUniform("shadow_tex", 1);
	}
	
	public void setProjectionView(Matrix4f projectionView) {
		setMatrix4f(load_projectionView, projectionView);
	}
	
	public void setShadowMapSpace(Matrix4f matrix) {
		setMatrix4f(load_toShadowMapSpace, matrix);
	}
}
