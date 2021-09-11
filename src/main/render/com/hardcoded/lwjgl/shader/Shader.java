package com.hardcoded.lwjgl.shader;

import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

/**
 * A simple shader implementation
 * 
 * @author HardCoded
 */
public abstract class Shader {
	/** Used to undefine a variable */
	protected static final Object UNDEFINE = new Object();
	/** Used to define an empty variable */
	protected static final Object DEFINE = "";
	
	private Map<String, Integer> uniforms = new HashMap<>();
	
	protected final int programId;
	protected int vertexShaderId;
	protected int fragmentShaderId;
	
	protected Shader(String vertexPath, String fragmentPath) {
		this(vertexPath, fragmentPath, Map.of());
	}
	
	protected Shader(String vertexPath, String fragmentPath, Map<String, Object> defines) {
		programId = GL20.glCreateProgram();
		if(programId == 0) {
			throw new ShaderException("Failed to create shader: GL20.glCreateProgram() returned 0");
		}
		
		try {
			InputStream vertexStream = Shader.class.getResourceAsStream(vertexPath);
			InputStream fragmentStream = Shader.class.getResourceAsStream(fragmentPath);
			createShader(replaceDefines(new String(vertexStream.readAllBytes()), defines), GL20.GL_VERTEX_SHADER);
			createShader(replaceDefines(new String(fragmentStream.readAllBytes()), defines), GL20.GL_FRAGMENT_SHADER);
			vertexStream.close();
			fragmentStream.close();
		} catch(IOException e) {
			throw new ShaderException("Failed to load vertex/fragment shader: " + e);
		}
		
		loadBinds();
		link();
		
		bind();
		loadUniforms();
		unbind();
	}
	
	private String replaceDefines(String code, Map<String, Object> defines) {
		String result = code;
		
		for(String key : defines.keySet()) {
			String pattern = "#define " + key + "[^\\r\\n]*";
			Object value = defines.get(key);
			
			if(value == UNDEFINE) {
				// Remove the valuee of the string value is blank
				result = result.replaceFirst(pattern, "");
			} else {
				result = result.replaceFirst(pattern, "#define " + key + " " + value);
			}
		}
		
		return result;
	}
	
	protected abstract void loadBinds();
	protected abstract void loadUniforms();
	
	private int createShader(String shaderCode, int shaderType) throws ShaderException {
		int shaderId = glCreateShader(shaderType);
		if(shaderId == 0) {
			throw new ShaderException("Error creating shader. Type: " + shaderType);
		}
		
		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);
		
		if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			System.out.println(shaderCode);
			throw new ShaderException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
		}
		
		glAttachShader(programId, shaderId);
		return shaderId;
	}
	
	protected final void bindAttrib(int index, String name) {
		GL20.glBindAttribLocation(programId, index, name);
	}
	
	protected final int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programId, uniformName);
	}
	
	protected void setMatrix4f(int uniformId, Matrix4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			GL20.glUniformMatrix4fv(uniformId, false, fb);
		}
	}
	
	protected final void setVector3f(int uniformId, Vector3f v) {
		GL20.glUniform3f(uniformId, v.x, v.y, v.z);
	}
	
	// OLD FUNCTIONS
	protected int createUniform(String uniformName) {
		int uniformLocation = glGetUniformLocation(programId, uniformName);
		uniforms.put(uniformName, uniformLocation);
		return uniformLocation;
	}
	
	protected void setUniform(String uniformName, Matrix4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(_uniform(uniformName), false, fb);
		}
	}
	
	protected void setUniform(String uniformName, Matrix3f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(12);
			value.get(fb);
			glUniformMatrix3fv(_uniform(uniformName), false, fb);
		}
	}
	
	protected void setUniform(String uniformName, Vector4f v) {
		glUniform4f(_uniform(uniformName), v.x, v.y, v.z, v.w);
	}
	
	protected void setUniform(String uniformName, Vector3f v) {
		glUniform3f(_uniform(uniformName), v.x, v.y, v.z);
	}
	
	protected void setUniform(String uniformName, Vector2f v) {
		glUniform2f(_uniform(uniformName), v.x, v.y);
	}
	
	protected void setUniform(String uniformName, int value) {
		glUniform1i(_uniform(uniformName), value);
	}
	
	protected void setUniform4i(String uniformName, int a, int b, int c, int d) {
		glUniform4i(_uniform(uniformName), a, b, c, d);
	}
	
	protected void setUniform(String uniformName, float x, float y, float z, float w) {
		glUniform4f(_uniform(uniformName), x, y, z, w);
	}
	
	protected void setUniform(String uniformName, float x, float y, float z) {
		glUniform3f(_uniform(uniformName), x, y, z);
	}
	
	protected void setUniform(String uniformName, float x, float y) {
		glUniform2f(_uniform(uniformName), x, y);
	}
	
	protected void setUniform(String uniformName, float x) {
		glUniform1f(_uniform(uniformName), x);
	}
	
	@Deprecated
	public void setUniform(String uniformName, boolean value) {
		glUniform1i(_uniform(uniformName), value ? 1 : 0);
	}
	
	private int _uniform(String uniformName) {
		if(!uniforms.containsKey(uniformName)) {
			createUniform(uniformName);
			return uniforms.get(uniformName);
		}
		
		return uniforms.get(uniformName);
	}
	
	private void link() throws ShaderException {
		glLinkProgram(programId);
		if(glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			throw new ShaderException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
		}
		
		if(vertexShaderId != 0) {
			glDetachShader(programId, vertexShaderId);
		}
		
		if(fragmentShaderId != 0) {
			glDetachShader(programId, fragmentShaderId);
		}
		
		glValidateProgram(programId);
		if(glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
		}
	}
	
	public final void bind() {
		glUseProgram(programId);
	}
	
	public final void unbind() {
		glUseProgram(0);
	}
	
	public final void cleanup() {
		unbind();
		if(programId != 0) {
			glDeleteProgram(programId);
		}
	}
	
	
	// Could be implemented
	
	public void setViewMatrix(Matrix4f matrix) {}
	public void setTranslationMatrix(Matrix4f matrix) {}
	public void setProjectionMatrix(Matrix4f matrix) {}
	public void setColor3f(float r, float g, float b) {}
	public void setColor4f(float r, float g, float b, float a) {}
}
