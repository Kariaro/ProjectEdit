package com.hardcoded.settings;

import static com.hardcoded.settings.SettingKey.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ProjectSettings {
	protected static final Logger LOGGER = LogManager.getLogger(ProjectSettings.class);
	private static final ProjectSettings instance = new ProjectSettings();
	
	private final Map<SettingKey, SettingProperty> properties;
	
	private ProjectSettings() {
		properties = new LinkedHashMap<>();
		for(SettingKey key : SettingKey.keys()) {
			properties.put(key, new SettingProperty(key));
		}
	}
	
//	private void resetValues() {
//		for(SettingProperty value : properties.values()) {
//			value.setObjectValue(value.getDefaultValue());
//		}
//	}
	
	private void storeValues(Properties prop) {
		for(SettingProperty value : properties.values()) {
			prop.setProperty(value.getFieldName(), value.getStringValue());
		}
	}
	
	private void loadValues(Properties prop) {
		for(SettingProperty value : properties.values()) {
			value.setStringValue(prop.getProperty(value.getFieldName()));
		}
	}
	
	public static final void load(String pathname) throws IOException {
		try(FileInputStream fs = new FileInputStream(pathname)) {
			Properties prop = new Properties();
			prop.load(fs);
			instance.loadValues(prop);
		} catch(IOException e) {
			LOGGER.error("Failed to load the config: '{}'", pathname);
			throw e;
		}
	}
	
	public static final void save(String pathname) throws IOException {
		try(FileOutputStream fs = new FileOutputStream(pathname)) {
			Properties prop = new Properties();
			instance.storeValues(prop);
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
		getProperty(RenderDistance).setObjectValue(distance);
	}
	
	public static int getMaxFps() {
		return getProperty(MaxFps).getIntegerValue();
	}
	
	public static void setMaxFps(int fps) {
		if(fps < 1) throw new RuntimeException("Invalid fps: " + fps);
		getProperty(MaxFps).setObjectValue(fps);
	}
	
	public static boolean useTransparentTextures() {
		return getProperty(AllowTransparentTextures).getBooleanValue();
	}
	
	public static void useTransparentTextures(boolean enable) {
		getProperty(AllowTransparentTextures).setObjectValue(enable);
	}
	
	public static boolean getRenderShadows() {
		return getProperty(RenderShadows).getBooleanValue();
	}
	
	public static void setRenderShadows(boolean enable) {
		getProperty(RenderShadows).setObjectValue(enable);
	}
}
