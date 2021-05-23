package com.hardcoded.mc.general;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.utils.NotNull;

/**
 * This class provies information about where the game is located and more.
 * 
 * @author HardCoded
 */
public class Minecraft {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(Minecraft.class);
	
	private Minecraft() {
		
	}
	
	@NotNull
	public static File getMinecraftPath() {
		return new File(System.getProperty("user.home"), "AppData/Roaming/.minecraft");
	}
	
	@NotNull
	public static File[] getSaves() {
		File[] array = new File(getMinecraftPath(), "saves").listFiles();
		return array == null ? new File[0]:array;
	}
}
