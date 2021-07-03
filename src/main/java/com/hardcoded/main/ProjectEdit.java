package com.hardcoded.main;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.api.IProjectEdit;
import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.icon.TextureManager;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.window.settings.LwjglSettingsWindow;

/**
 * This is the initial starting point for the editor application.
 * 
 * @author HardCoded
 */
public class ProjectEdit implements IProjectEdit {
	private static final Logger LOGGER = LogManager.getLogger(ProjectEdit.class);
	private static final Unsafe unsafe = new Unsafe();
	private static ProjectEdit instance;
	
	public static final String VERSION = "0.0.2";
	
	private LwjglWindow window;
	private LwjglSettingsWindow settings_window;
	
	private TextureManager textureManager;
	private World world;
	private Camera camera;
	
	protected ProjectEdit() {
		instance = this;
		camera = new Camera();
		window = new LwjglWindow();
		settings_window = new LwjglSettingsWindow();
		textureManager = new TextureManager();
	}
	
	// Only callable from package
	protected void start() {
		LOGGER.info("Starting application");
		window.start();
	}
	
	@Override
	public World loadWorld(File file) {
		if(instance.world != null) {
			// TODO: Unload previously loaded world
		}
		
		World world = new World(file);
		instance.world = world;
		return world;
	}

	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public Camera getCamera() {
		return camera;
	}
	
	@Override
	public TextureManager getTextureManager() {
		return textureManager;
	}
	
	/**
	 * Internal getter for the main window
	 */
	public LwjglWindow getWindow() {
		return window;
	}
	
	/**
	 * Internal getter for the settings window
	 */
	public LwjglSettingsWindow getSettingsWindow() {
		return settings_window;
	}
	
	/**
	 * Returns the current instance of {@code ProjectEdit}
	 */
	public static IProjectEdit getInstance() {
		return instance;
	}
	
	/**
	 * Close all resources used by ProjectEdit
	 */
	public static void shutdown() {
		instance.settings_window.cleanup();
	}
	
	public static Unsafe getUnsafe() {
		return unsafe;
	}
	
	/**
	 * Returns the current version of this software
	 */
	public static String getVersionString() {
		return VERSION;
	}
	
	public static final class Unsafe {
		private Unsafe() {}
		
	}
}
