package com.hardcoded.mc.general.world;

import java.io.File;
import java.util.Set;

import com.hardcoded.mc.general.files.IChunk;
import com.hardcoded.mc.general.nbt.NBTTagCompound;
import com.hardcoded.mc.general.nbt.NBTTagString;
import com.hardcoded.utils.NotNull;

public class World {
	private final File file;
	private final NBTTagCompound level_dat;
	private final NBTTagCompound version;
	private final ChunkProvider chunkProvider;
	
	public World(File file) {
		this.file = file;
		this.level_dat = WorldLoader.loadLevelDat(this);
		this.version = (NBTTagCompound)((NBTTagCompound)level_dat.get("Data")).get("Version");
		this.chunkProvider = new ChunkProvider(this);
	}
	
	public void unloadRegionsNotFound(Set<Long> set) {
		chunkProvider.unloadRegionsNotFound(set);
	}
	
	public IChunk getChunk(int x, int z) {
		return chunkProvider.getChunk(x, z);
	}
	
	@NotNull
	public IBlockData getBlock(int x, int y, int z) {
		return getChunk(Math.floorDiv(x, 16), Math.floorDiv(z, 16)).getBlock(x & 15, y, z & 15);
	}
	
	protected File getFile() {
		return file;
	}
	
	public ChunkProvider getChunkProvider() {
		return chunkProvider;
	}
	
	public String getVersion() {
		return ((NBTTagString)version.get("Name")).getValue();
	}
}
