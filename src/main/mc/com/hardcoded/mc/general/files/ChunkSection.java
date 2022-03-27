package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;

public class ChunkSection implements IChunkSection {
	public IBlockData[] blocks;
	public Status status;
	public final int y;
	
	public ChunkSection(int y) {
		this.status = Status.UNLOADED;
		this.y = y;
	}
	
	@Override
	public IBlockData getBlock(int x, int y, int z) {
		if(!isLoaded()) return Blocks.get(Blocks.AIR);
		
		IBlockData block = blocks[(x) | (z << 4) | (y << 8)];
		return (block == null) ? Blocks.get(Blocks.AIR):block;
	}
	
	@Override
	public void setBlock(IBlockData state, int x, int y, int z) {
		if(!isLoaded()) return;
		blocks[(x) | (z << 4) | (y << 8)] = state;
	}
	
	@Override
	public Status getStatus() {
		return status;
	}
	
	@Override
	public boolean isLoaded() {
		return status == Status.LOADED;
	}
	
	@Override
	public int getY() {
		return y;
	}
}
