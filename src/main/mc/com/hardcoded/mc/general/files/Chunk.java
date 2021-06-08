package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;

public class Chunk implements IChunk {
	private ChunkSection[] sections = new ChunkSection[16];
	
	// Chunk x coordinate
	public int x;
	
	// Chunk z coordinate
	public int z;
	
	public Chunk() {
		for(int i = 0, len = sections.length; i < len; i++) {
			sections[i] = new ChunkSection();
		}
	}
	
	@Override
	public IBlockData getBlock(int x, int y, int z) {
		return getSection(y / 16).getBlock(x & 15, y & 15, z & 15);
	}
	
	public void setBlock(IBlockData state, int x, int y, int z) {
		getSection(y / 16).setBlock(state, x & 15, y & 15, z & 15);
	}
	
	public IChunkSection getSection(int y) {
		if(y < 0 || y >= sections.length) return IChunkSection.UNLOADED;
		return sections[y];
	}

	@Override
	public boolean isLoaded() {
		return true;
	}
}
