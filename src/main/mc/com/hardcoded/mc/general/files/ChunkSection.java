package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;

public class ChunkSection implements IChunkSection {
	public IBlockData[] blocks;
	
	public ChunkSection() {
		blocks = new IBlockData[4096];
	}
	
	public IBlockData getBlock(int x, int y, int z) {
		IBlockData block = blocks[(x) | (z << 4) | (y << 8)];
		return (block == null) ? Blocks.AIR:block;
	}
	
	public void setBlock(IBlockData state, int x, int y, int z) {
		blocks[(x) | (z << 4) | (y << 8)] = state;
	}
	
	@Override
	public boolean isLoaded() {
		return true;
	}
}
