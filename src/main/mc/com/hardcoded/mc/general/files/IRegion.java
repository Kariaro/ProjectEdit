package com.hardcoded.mc.general.files;

public interface IRegion {
	Status getStatus();
	
	IChunk getChunk(int x, int z);
	
	boolean hasChunk(int x, int z);
	
	int getX();
	int getZ();
	long getRegionIndex();
}
