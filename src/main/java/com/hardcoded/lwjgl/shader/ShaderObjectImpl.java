package com.hardcoded.lwjgl.shader;

import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * This abstract class adds light and transformation access to shaders.
 * 
 * @author HardCoded
 * @since v0.2
 */
public abstract class ShaderObjectImpl extends Shader {
	protected static final Vector3f DEFAULT_EMPTY = new Vector3f();
	
	protected int load_toShadowMapSpace;
//	protected int load_transformationMatrix;
	protected int load_projectionView;
//	protected int load_lightDirection;
//	protected int load_modelMatrix;
//	protected int load_viewMatrix;
	
	protected ShaderObjectImpl(String vertex, String fragment) {
		super(vertex, fragment, Map.of(
		));
	}
	
	@Override
	protected void loadUniforms() {
		load_toShadowMapSpace = getUniformLocation("toShadowMapSpace");
		load_projectionView = getUniformLocation("projectionView");

		setUniform("dif_tex", 0);
		setUniform("shadow_tex", 1);
		
//		load_lightPositionViewSpace = new int[LwjglSettings.MAX_LIGHTS];
//		//load_lightColor = new int[LwjglOptions.MAX_LIGHTS];
//		for(int i = 0; i < LwjglSettings.MAX_LIGHTS; i++) {
//			load_lightPositionViewSpace[i] = getUniformLocation("load_lightPositionViewSpace[" + i + "]");
//			//load_lightColor[i] = getUniformLocation("load_lightColor[" + i + "]");
//		}
	}
	
	public void setProjectionView(Matrix4f projectionView) {
		setMatrix4f(load_projectionView, projectionView);
	}
//	
//	public void setLightDirection(Matrix4f lightDirection) {
//		setMatrix4f(load_lightDirection, lightDirection);
//		// setVector3f(load_lightDirection, lightDirection);
//	}
//	
//	public void setModelMatrix(Matrix4f modelMatrix) {
//		setMatrix4f(load_modelMatrix, modelMatrix);
//	}
//	
//	public void setViewMatrix(Matrix4f viewMatrix) {
//		setMatrix4f(load_viewMatrix, viewMatrix);
//	}
	
	public void setShadowMapSpace(Matrix4f matrix) {
		setMatrix4f(load_toShadowMapSpace, matrix);
	}
}
