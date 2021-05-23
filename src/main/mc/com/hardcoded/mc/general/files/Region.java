package com.hardcoded.mc.general.files;

public class Region implements IRegion {
	public IChunk[] chunks = new IChunk[1024]; // 32x32
	
	@Override
	public IChunk getChunk(int x, int z) {
		return chunks[x + z * 32];
	}
	
	@Override
	public boolean hasChunk(int x, int z) {
		return getChunk(x, z) != null;
	}
	
	@Override
	public boolean isLoaded() {
		return true;
	}
}
