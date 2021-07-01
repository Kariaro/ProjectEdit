package com.hardcoded.mc.general.files;

import java.util.HashMap;
import java.util.Map;

import com.hardcoded.mc.general.world.*;

public class Chunk implements IChunk {
	public final Map<Integer, ChunkSection> sections;
	
	// Convert these into a pair
	private final long chunk_pair;
	private final int chunk_x;
	private final int chunk_z;
	
	public boolean isDirty;
	public Status status;
	public BiomeReader biomeReader;
	
	public Chunk(int x, int z, Status status) {
		if(status == Status.FAILED) {
			// Make the map immutable
			this.sections = Map.of();
			this.status = Status.FAILED;
		} else {
			this.status = Status.UNLOADED;
			this.sections = new HashMap<>();
		}
		
		this.chunk_x = x;
		this.chunk_z = z;
		this.chunk_pair = ((long)(x) & 0xffffffffL) | (((long)z) << 32L);
	}
	
	@Override
	public IBlockData getBlock(int x, int y, int z) {
		IChunkSection section = sections.get(y / 16);
		if(section == null || y < 0) return Blocks.VOID_AIR;
		
		return section.getBlock(x & 15, y & 15, z & 15);
	}
	
	public void setBlock(IBlockData state, int x, int y, int z) {
		IChunkSection section = sections.get(y / 16);
		if(section != null) {
			section.setBlock(state, x & 15, y & 15, z & 15);
			
			// Mark this chunk as dirty
			isDirty = true;
		}
	}
	
	@Override
	public IChunkSection getSection(int y) {
		return sections.get(y);
	}
	
	@Override
	public Biome getBiome(int x, int y, int z) {
		if(y < 0) return Biomes.THE_VOID;
		return biomeReader.getBiome((x & 15) / 4, y / 4, (z & 15) / 4);
	}
	
	@Override
	public void setBiome(Biome biome, int x, int y, int z) {
		throw new UnsupportedOperationException();
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
	public long getPair() {
		return chunk_pair;
	}
	
	@Override
	public World getWorld() {
		return null;
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
	public int hashCode() {
		// Chunks that are 1 million blocks appart in x or z will have the same hash
		return (((chunk_x >>> 16) ^ (chunk_x & 65535)) << 16)
			 | (((chunk_z >>> 16) ^ (chunk_z & 65535)));
	}
	
	/**
	 * @deprecated
	 * 		There could be multiple chunks occupying the same area
	 * 		when editing because of editing statuses. This method
	 * 		could not determine the true equality of chunks.
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Chunk)) return false;
		Chunk that = (Chunk)obj;
		return this.chunk_x == that.chunk_x
			|| this.chunk_z == that.chunk_z;
	}
	
	@Override
	public String toString() {
		return String.format("%s@[%d,%d,%s]", Chunk.class.getName(), chunk_x, chunk_z, status);
	}
}
