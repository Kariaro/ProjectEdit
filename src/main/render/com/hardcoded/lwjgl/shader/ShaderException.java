package com.hardcoded.lwjgl.shader;

/**
 * A shader exception
 * 
 * @author HardCoded
 */
public class ShaderException extends RuntimeException {
	private static final long serialVersionUID = 2713642204792508687L;
	
	public ShaderException(String message) {
		super(message);
	}
}
