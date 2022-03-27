package com.hardcoded.mc.general.world;

import java.io.File;
import java.util.Set;

import com.hardcoded.api.Nonnull;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.files.IChunk;
import com.hardcoded.mc.general.nbt.NBTTagCompound;
import com.hardcoded.mc.general.nbt.NBTTagString;

public class World {
	private final File folder;
	private final NBTTagCompound level_dat;
	private final NBTTagCompound version;
	private final ChunkProvider chunkProvider;
	private final String versionString;
	
	public World(File file) {
		this.folder = file;
		this.level_dat = WorldLoader.loadLevelDat(this);
		this.version = (NBTTagCompound)((NBTTagCompound)level_dat.get("Data")).get("Version");
		this.chunkProvider = new ChunkProvider(this);
		this.versionString = ((NBTTagString)version.get("Name")).getValue();
	}
	
	public void unloadRegionsNotFound(Set<Long> set) {
		chunkProvider.unloadRegionsNotFound(set);
	}
	
	public IChunk getChunk(int x, int z) {
		return chunkProvider.getChunk(x, z);
	}
	
	@Nonnull
	public IBlockData getBlock(int x, int y, int z) {
		IChunk chunk = getChunk(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
		if(chunk == null) return Blocks.get(Blocks.VOID_AIR);
		return chunk.getBlock(x & 15, y, z & 15);
	}
	
	public void setBlock(IBlockData state, int x, int y, int z) {
		getChunk(Math.floorDiv(x, 16), Math.floorDiv(z, 16)).setBlock(state, x & 15, y, z & 15);
	}
	
	public File getFolder() {
		return folder;
	}
	
	public ChunkProvider getChunkProvider() {
		return chunkProvider;
	}
	
	public String getVersion() {
		return versionString;
	}
}
