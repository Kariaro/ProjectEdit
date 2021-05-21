package com.hardcoded.lwjgl.util;

/**
 * A render exception.
 * 
 * @author HardCoded
 */
public class RenderException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public RenderException() {
		
	}
	
	public RenderException(String message) {
		super(message);
	}
}
