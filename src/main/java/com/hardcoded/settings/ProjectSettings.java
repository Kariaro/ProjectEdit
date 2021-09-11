package com.hardcoded.settings;

import static com.hardcoded.settings.SettingKey.*;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ProjectSettings {
	protected static final Logger LOGGER = LogManager.getLogger(ProjectSettings.class);
	private static final String PROPERTIES_PATH = System.getProperty("user.home") + File.separatorChar + ".ProjectEdit.properties";
	private static final ProjectSettings instance = new ProjectSettings();
	
	private final Map<SettingKey, SettingProperty> properties;
	
	
	
	private ProjectSettings() {
		properties = new LinkedHashMap<>();
		for(SettingKey key : SettingKey.keys()) {
			properties.put(key, new SettingProperty(key));
		}
		
		try {
			load(PROPERTIES_PATH);
		} catch(Exception ignore) {
			
		}
	}
	

	public static void save() {
		try {
			instance.save(PROPERTIES_PATH);
		} catch(Exception ignore) {
			
		}
	}
	
//	private void resetValues() {
//		for(SettingProperty value : properties.values()) {
//			value.setObjectValue(value.getDefaultValue());
//		}
//	}
	
	private void storeValues(Properties prop) {
		for(SettingProperty value : properties.values()) {
			prop.setProperty(value.getFieldName(), value.getStringDataValue());
		}
	}
	
	private void loadValues(Properties prop) {
		for(SettingProperty value : properties.values()) {
			value.setStringValue(prop.getProperty(value.getFieldName()));
		}
	}
	
	private void load(String pathname) throws IOException {
		try(FileInputStream fs = new FileInputStream(pathname)) {
			Properties prop = new Properties();
			prop.load(fs);
			loadValues(prop);
		} catch(IOException e) {
			LOGGER.error("Failed to load the config: '{}'", pathname);
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void save(String pathname) throws IOException {
		try(FileOutputStream fs = new FileOutputStream(pathname)) {
			Properties prop = new Properties();
			storeValues(prop);
			prop.store(fs, "");
		} catch(IOException e) {
			LOGGER.error("Failed to load the config: '{}'", pathname);
			throw e;
		}
	}
	
	private static SettingProperty getProperty(SettingKey key) {
		return instance.properties.get(key);
	}
	
	
	public static float getFov() {
		return getProperty(Fov).getFloatValue();
	}
	
	public static void setFov(float fov) {
		getProperty(Fov).setObjectValue(fov);
	}
	
	public static int getRenderDistance() {
		return getProperty(RenderDistance).getIntegerValue();
	}
	
	public static void setRenderDistance(int distance) {
		if(distance < 1 || distance > 128) throw new RuntimeException("Invalid render distance: " + distance);
		setKeyValue(RenderDistance, distance);
	}
	
	public static int getMaxFps() {
		return getProperty(MaxFps).getIntegerValue();
	}
	
	public static void setMaxFps(int fps) {
		if(fps < 1) throw new RuntimeException("Invalid fps: " + fps);
		setKeyValue(MaxFps, fps);
	}
	
	public static boolean useTransparentTextures() {
		return getProperty(AllowTransparentTextures).getBooleanValue();
	}
	
	public static void useTransparentTextures(boolean enable) {
		setKeyValue(AllowTransparentTextures, enable);
	}
	
	public static boolean getRenderShadows() {
		return getProperty(RenderShadows).getBooleanValue();
	}
	
	public static void setRenderShadows(boolean enable) {
		setKeyValue(RenderShadows, enable);
	}
	
	/**
	 * Set the value of specified key
	 * 
	 * @param key the setting key
	 * @param value the new value of the setting
	 * @throws NullPointerException	If {@code key} or {@code value} was {@code null}
	 */
	public static void setKeyValue(SettingKey key, Object value) {
		if(key == null || value == null)
			throw new NullPointerException("key or value must not be null");
		
		try {
			getProperty(key).setObjectValue(value);
			ProjectSettings.save();
		} catch(SettingException e) {
			LOGGER.throwing(e);
		}
	}
	
	public static Object getKeyValue(SettingKey key) {
		return getProperty(key).getObjectValue();
	}
}
