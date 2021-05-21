package com.hardcoded.lwjgl.util;

/**
 * This exception is used when something goes wrong during object loading.
 * 
 * @author HardCoded
 */
public class LoadingException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public LoadingException() {
		
	}
	
	public LoadingException(String message) {
		super(message);
	}
}
