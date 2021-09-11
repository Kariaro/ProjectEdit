package com.hardcoded.lwjgl.shader;

import java.nio.FloatBuffer;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

public class UniversalShader extends Shader {
	public static final int HAS_TEXTURE = 1;
	public static final int HAS_COLOR = 2;
	
	protected int load_projectionMatrix;
	protected int load_translationMatrix;
	
	
	protected int load_uniformColor;
	
	public UniversalShader(int flags) {
		super(
			"/shaders/universal/vert.vs",
			"/shaders/universal/frag.fs",
			Map.of(
				"HAS_TEXTURE", (flags & HAS_TEXTURE) != 0 ? DEFINE:UNDEFINE,
				"HAS_COLOR", (flags & HAS_COLOR) != 0 ? DEFINE:UNDEFINE
			)
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
		load_projectionMatrix = getUniformLocation("projectionMatrix");
		load_translationMatrix = getUniformLocation("translationMatrix");
		load_uniformColor = getUniformLocation("uniform_Color");
		
		GL20.glUniform1i(getUniformLocation("dif_tex"), 0);
	}

	private final FloatBuffer buffer = MemoryUtil.memAllocFloat(16);
	
	@Override
	public void setProjectionMatrix(Matrix4f matrix) {
		matrix.get(buffer);
		GL20.glUniformMatrix4fv(load_projectionMatrix, false, buffer);
	}
	
	@Override
	public void setTranslationMatrix(Matrix4f matrix) {
		matrix.get(buffer);
		GL20.glUniformMatrix4fv(load_translationMatrix, false, buffer);
	}
	
	@Override
	public void setColor3f(float r, float g, float b) {
		setColor4f(r, g, b, 1.0f);
	}
	
	@Override
	public void setColor4f(float r, float g, float b, float a) {
		GL20.glUniform4f(load_uniformColor, r, g, b, a);
	}
}
