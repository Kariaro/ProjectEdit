package com.hardcoded.api;

import java.io.File;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.utils.Nonnull;
import com.hardcoded.utils.Nullable;

/**
 * Interface describing the ProjectEdit api
 * 
 * @author HardCoded
 */
public interface IProjectEdit {
	
	/**
	 * Returns the current camera used by the editor
	 */
	@Nonnull
	Camera getCamera();
	
	/**
	 * Returns the current world loaded by the editor.
	 * If no world is loaded {@code null} will be returned
	 */
	@Nullable
	World getWorld();
	
	/**
	 * Load a world from a world folder
	 * 
	 * @param file the folder of the world
	 * @return the loaded world
	 */
	World loadWorld(File file);
}
