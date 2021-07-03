package com.hardcoded.lwjgl.shader;

import java.util.Map;

import org.joml.Matrix4f;

/**
 * @author HardCoded
 */
public abstract class ShaderObjectImpl extends Shader {
	protected int load_translationMatrix;
	
	protected ShaderObjectImpl(String vertex, String fragment) {
		super(vertex, fragment, Map.of());
	}
	
	@Override
	protected void loadUniforms() {
		load_translationMatrix = getUniformLocation("translationMatrix");
	}
	
	public void setTranslationMatrix(Matrix4f matrix) {
		setMatrix4f(load_translationMatrix, matrix);
	}
}
