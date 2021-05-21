package com.hardcoded.mc.general;

import java.io.File;

/**
 * This interface contains information about a saved minecraft world.
 * 
 * <p>This class is an interface because save files could potentially
 * be version dependant.
 * 
 * @author HardCoded
 */
public interface MC_Save {
	
	/**
	 * @return the folder that contains this save
	 */
	File getFolder();
}
