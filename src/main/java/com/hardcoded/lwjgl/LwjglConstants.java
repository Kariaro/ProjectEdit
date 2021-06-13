package com.hardcoded.lwjgl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A constant singleton
 */
public class LwjglConstants {
	private static final Logger LOGGER = LogManager.getLogger(LwjglConstants.class);
	private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#.#####", DecimalFormatSymbols.getInstance(Locale.US));
	private static final LwjglConstants instance = new LwjglConstants();
	
	// max_fps
	private static final String KEY_FOV = "fov";
	private static final String KEY_MAX_FPS = "max_fps";
	private static final String KEY_RENDER_DISTANCE = "render_distance";
	
	private static final float DEFAULT_FOV = 90;
	private static final int DEFAULT_MAX_FPS = 120;
	private static final int DEFAULT_RENDER_DISTANCE = 8;
	
	
	// fields
	private int max_fps;
	private int renderDistance;
	private float fov;
	
	private LwjglConstants() {
		resetValues();
	}
	
	private void resetValues() {
		fov = DEFAULT_FOV;
		renderDistance = DEFAULT_RENDER_DISTANCE;
		max_fps = DEFAULT_MAX_FPS;
	}
	
	private void storeValues(Properties prop) {
		prop.setProperty(KEY_FOV, FLOAT_FORMAT.format(fov));
		prop.setProperty(KEY_RENDER_DISTANCE, Integer.toString(renderDistance));
		prop.setProperty(KEY_MAX_FPS, Integer.toString(max_fps));
	}
	
	private void loadValues(Properties prop) {
		fov = Float.valueOf(prop.getProperty(KEY_FOV));
		renderDistance = Integer.valueOf(prop.getProperty(KEY_RENDER_DISTANCE));
		max_fps = Integer.valueOf(prop.getProperty(KEY_MAX_FPS));
	}
	
	
	// GLOBAL METHODS //
	
	/**
	 * Load all constants from a file.
	 * 
	 * @param pathname the path to the configuration file
	 */
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
	
	/**
	 * Save all constants.
	 * 
	 * @param pathname the path to the configuration file
	 */
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
	
	public static float getFov() {
		return instance.fov;
	}
	
	public static void setFov(float fov) {
		instance.fov = fov;
	}
	
	public static int getRenderDistance() {
		return instance.renderDistance;
	}
	
	public static void setRenderDistance(int distance) {
		if(distance < 1 || distance > 128) throw new RuntimeException("Invalid render distance: " + distance);
		instance.renderDistance = distance;
	}
	
	public static int getMaxFps() {
		return instance.max_fps;
	}
	
	public static void setMaxFps(int fps) {
		if(fps < 1) throw new RuntimeException("Invalid fps: " + fps);
		instance.max_fps = fps;
	}
}
