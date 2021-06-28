package com.hardcoded.mc.general;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.api.Nonnull;

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
	
	@Nonnull
	public static File getMinecraftPath() {
		return new File(System.getProperty("user.home"), "AppData/Roaming/.minecraft");
	}
	
	@Nonnull
	public static File[] getSaves() {
		File[] array = new File(getMinecraftPath(), "saves").listFiles();
		return array == null ? new File[0]:array;
	}
	
	@Nonnull
	public static File getVersionsPath() {
		return new File(getMinecraftPath(), "versions");
	}
	
	@Nonnull
	public static File getVersionFolder(String name) {
		return new File(getMinecraftPath(), "versions/" + name);
	}
	
	@Nonnull
	public static File getSave(String name) {
		return new File(getMinecraftPath(), "saves/" + name);
	}
}
