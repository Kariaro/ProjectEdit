package com.hardcoded.settings;

/**
 * This exception is thrown when a setting was wrongly configured
 * 
 * @author HardCoded
 */
public class SettingException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final SettingKey key;
	
	public SettingException(SettingKey key) {
		super("Error configuring key " + key);
		this.key = key;
	}
	
	public SettingException(SettingKey key, Throwable cause) {
		super(cause);
		this.key = key;
	}
	
	public SettingException(SettingKey key, String message) {
		super(message);
		this.key = key;
	}
	
	public SettingException(SettingKey key, String message, Throwable cause) {
		super(message, cause);
		this.key = key;
	}
	
	public SettingKey getSettingKey() {
		return key;
	}
}
