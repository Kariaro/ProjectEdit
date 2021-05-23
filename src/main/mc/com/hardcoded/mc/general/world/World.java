package com.hardcoded.mc.general.world;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.nbt.NBTTagCompound;
import com.hardcoded.mc.general.nbt.NBTTagString;
import com.hardcoded.utils.NotNull;

public class World {
	protected Map<Long, IRegion> regions = new HashMap<>();	
	private final File file;
	private final NBTTagCompound level_dat;
	private final NBTTagCompound version;
	
	public World(File file) {
		this.file = file;
		this.level_dat = WorldLoader.loadLevelDat(this);
		this.version = (NBTTagCompound)((NBTTagCompound)level_dat.get("Data")).get("Version");
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
	
	@NotNull
	public IBlockState getBlock(int x, int y, int z) {
		return getWorldRegion(x, z)
			  .getChunk(Math.floorDiv(x, 16) & 31, Math.floorDiv(z, 16) & 31)
			  .getBlock(x & 15, y, z & 15);
	}
	
	protected File getFile() {
		return file;
	}
	
	public String getVersion() {
		return ((NBTTagString)version.get("Name")).getValue();
	}
	
	private static long get_index(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
}
