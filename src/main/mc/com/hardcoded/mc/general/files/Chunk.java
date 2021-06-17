package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;

public class Chunk implements IChunk {
	private ChunkSection[] sections = new ChunkSection[16];
	
	public final int chunk_x;
	public final int chunk_z;
	public boolean isDirty;
	public Status status;
	
	public Chunk(int x, int z) {
		for(int i = 0, len = sections.length; i < len; i++) {
			sections[i] = new ChunkSection();
		}
		
		this.chunk_x = x;
		this.chunk_z = z;
	}
	
	@Override
	public IBlockData getBlock(int x, int y, int z) {
		if(y < 0) return Blocks.VOID_AIR;
		return getSection(y / 16).getBlock(x & 15, y & 15, z & 15);
	}
	
	public void setBlock(IBlockData state, int x, int y, int z) {
		getSection(y / 16).setBlock(state, x & 15, y & 15, z & 15);
		
		// Mark this chunk as dirty
		isDirty = true;
	}
	
	public IChunkSection getSection(int y) {
		if(y < 0 || y >= sections.length) return IChunkSection.UNLOADED;
		return sections[y];
	}
	
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	
	@Override
	public int getX() {
		return chunk_x;
	}
	
	@Override
	public int getZ() {
		return chunk_z;
	}
	
	@Override
	public World getWorld() {
		return null;
	}
	
	@Override
	public boolean isLoaded() {
		return true;
	}
	
	@Override
	public int hashCode() {
		// Chunks that are 1 million blocks appart in x or z will have the same hash
		return (((chunk_x >>> 16) ^ (chunk_x & 65535)) << 16)
			 | (((chunk_z >>> 16) ^ (chunk_z & 65535)));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Chunk)) return false;
		return hashCode() == obj.hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
