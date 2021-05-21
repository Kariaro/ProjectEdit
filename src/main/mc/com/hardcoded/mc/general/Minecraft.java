package com.hardcoded.mc.general;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provies information about where the game is located and more.
 * 
 * @author HardCoded
 */
public class Minecraft {
	private static final Logger LOGGER = LogManager.getLogger(Minecraft.class);
	private Minecraft() {
		
	}
	
	public static File getMinecraftPath() {
		return new File(System.getProperty("user.home"), "AppData/Roaming/.minecraft");
	}
}
