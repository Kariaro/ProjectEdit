package com.hardcoded.main;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.api.IProjectEdit;
import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.icon.TextureManager;
import com.hardcoded.mc.general.world.World;

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
	private TextureManager textureManager;
	private World world;
	private Camera camera;
	
	protected ProjectEdit() {
		instance = this;
		camera = new Camera();
		window = new LwjglWindow();
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
	
	public LwjglWindow getWindow() {
		return window;
	}
	
	@Override
	public TextureManager getTextureManager() {
		return textureManager;
	}
	
	/**
	 * Returns the current instance of {@code ProjectEdit}
	 */
	public static IProjectEdit getInstance() {
		return instance;
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
