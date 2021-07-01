package com.hardcoded.mc.general.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.api.Nonnull;
import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.nbt.NBTBase;
import com.hardcoded.mc.general.nbt.NBTTagCompound;
import com.hardcoded.util.StreamUtils;

public class WorldLoader {
	private static final Logger LOGGER = LogManager.getLogger(WorldLoader.class);
	
	@Nonnull
	public static NBTTagCompound loadLevelDat(World world) {
		File file = new File(world.getFolder(), "level.dat");
		
		if(!file.exists()) {
			return new NBTTagCompound();
		}
		
		try(FileInputStream stream = new FileInputStream(file)) {
			return NBTBase.readNBTTagCompound(ByteBuf.readOnly(StreamUtils.decompress_gzip(stream.readAllBytes())));
		} catch(IOException e) {
			LOGGER.error(e);
		}
		
		return new NBTTagCompound();
	}
}
