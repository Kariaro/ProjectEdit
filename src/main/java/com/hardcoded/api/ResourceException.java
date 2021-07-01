package com.hardcoded.api;

/**
 * A generic resource exception thrown when failing to load resources.
 * 
 * This exception is thrown when {@code IResource} methods fail to load
 * resources.
 * 
 * @author HardCoded
 * 
 * @see com.hardcoded.api.IResource#reload()
 * @see com.hardcoded.api.IResource#unload()
 * 
 */
public class ResourceException extends RuntimeException {
	private static final long serialVersionUID = 4282292514629339647L;
	
	public ResourceException() {
		
	}
	
	public ResourceException(String format, Object... args) {
		super(String.format(format, args));
	}
	
	public ResourceException(Throwable t) {
		super(t);
	}
}
