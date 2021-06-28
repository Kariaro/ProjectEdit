package com.hardcoded.mc.general.files;

import com.hardcoded.api.Nullable;
import com.hardcoded.mc.general.world.Biome;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;

public interface IChunk {
	World getWorld();
	
	IBlockData getBlock(int x, int y, int z);
	
	void setBlock(IBlockData state, int x, int y, int z);
	
	Status getStatus();
	
	boolean isLoaded();
	
	boolean isDirty();
	
	int getX();
	int getZ();
	long getPair();
	
	/**
	 * Get the chunk section at the specified y coordinate
	 * @param y the y coordinate
	 */
	@Nullable
	IChunkSection getSection(int y);
	
	Biome getBiome(int x, int y, int z);
	
	void setBiome(Biome biome, int x, int y, int z);
}
