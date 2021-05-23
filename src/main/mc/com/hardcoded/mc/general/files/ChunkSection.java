package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockState;

public class ChunkSection implements IChunkSection {
	public IBlockState[] blocks;
	
	public ChunkSection() {
		blocks = new IBlockState[4096];
	}
	
	public IBlockState getBlock(int x, int y, int z) {
		IBlockState block = blocks[(x) | (z << 4) | (y << 8)];
		return (block == null) ? Blocks.AIR:block;
	}
	
	public void setBlock(IBlockState state, int x, int y, int z) {
		blocks[(x) | (z << 4) | (y << 8)] = state;
	}
}
