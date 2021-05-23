package com.hardcoded.mc.general.world;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.hardcoded.mc.general.files.*;
import com.hardcoded.utils.NotNull;

public class World {
	protected Map<Long, IRegion> regions = new HashMap<>();	
	private File file;
	
	public World(File file) {
		this.file = file;
	}
	
	/**
	 * Get the world region at the specified world coordinates.
	 * 
	 * @param x the x world coordinate
	 * @param z the y world coordinate
	 * @return the region at the specified world coordinates
	 */
	protected IRegion getWorldRegion(int x, int z) {
		final int region_x = Math.floorDiv(x, 512);
		final int region_z = Math.floorDiv(z, 512);
		
		// Get the index of this region
		final long index = get_index(region_x, region_z);
		
		IRegion region = regions.get(index);
		if(region == null) {
			region = WorldLoader.loadRegion(this, region_x, region_z);
			regions.put(index, region);
			return region;
		}
		
		return region;
	}
	
	protected IRegion getRegion(int x, int z) {
		// Get the index of this region
		final long index = get_index(x, z);
		
		IRegion region = regions.get(index);
		if(region == null) {
			region = WorldLoader.loadRegion(this, x, z);
			regions.put(index, region);
			return region;
		}
		
		return region;
	}
	
	public IChunk getChunk(int x, int z) {
		return getRegion(Math.floorDiv(x, 32), Math.floorDiv(z, 32)).getChunk(x & 31, z & 31);
	}
	
//	@NotNull
//	public IBlockState getBlock(BlockPosition pos) {
//		//return Math.random() > 0.1 ? Blocks.DIRT:Blocks.AIR;
//		//return getChunk(pos.getChunkX(), pos.getChunkZ()).getBlock(pos.getLocalChunkX(), pos.getLocalChunkY(), pos.getLocalChunkZ());
//		return null;
//	}
	
	@NotNull
	public IBlockState getBlock(int x, int y, int z) {
		return getWorldRegion(x, z)
			  .getChunk(Math.floorDiv(x, 16) & 31, Math.floorDiv(z, 16) & 31)
			  .getBlock(x & 15, y, z & 15);
	}
	
//	public void setBlock(IBlockState state, BlockPosition pos) {
//		getChunk(pos.getChunkX(), pos.getChunkZ()).setBlock(state, pos.getLocalChunkX(), pos.getLocalChunkY(), pos.getLocalChunkZ());
//	}
	
	protected File getFile() {
		return file;
	}
	
	private static long get_index(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
}
