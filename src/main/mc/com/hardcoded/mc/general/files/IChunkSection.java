package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;

public interface IChunkSection {
	IBlockData getBlock(int x, int y, int z);
	
	void setBlock(IBlockData state, int x, int y, int z);
	
	boolean isLoaded();
	
	Status getStatus();
	
	int getY();
}
