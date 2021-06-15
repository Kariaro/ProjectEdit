package com.hardcoded.main;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.api.IProjectEdit;
import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.mc.general.world.World;

/**
 * This is the initial starting point for the editor application.
 * 
 * @author HardCoded
 */
public class ProjectEdit implements IProjectEdit {
	private static final Logger LOGGER = LogManager.getLogger(ProjectEdit.class);
	private static ProjectEdit instance;
	
	private LwjglWindow window;
	private World world;
	private Camera camera;
	
	public static void main(String[] args) {
		LOGGER.info("Starting application");
		
		new ProjectEdit()
			.start();
	}
	
	public ProjectEdit() {
		instance = this;
		camera = new Camera();
		window = new LwjglWindow();
	}
	
	public void start() {
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
	
	public World getWorld() {
		return world;
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public LwjglWindow getWindow() {
		return window;
	}
	
	public static ProjectEdit getInstance() {
		return instance;
	}
}
