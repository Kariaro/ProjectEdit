package com.hardcoded.mc.general.world;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.mc.general.files.*;

/**
 * Loads chunks
 */
public class ChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger(ChunkProvider.class);
	
	protected Map<Long, IRegion> regions = new HashMap<>();	
	private World world;
	
	public ChunkProvider(World world) {
		this.world = world;
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
		return getRegion(region_x, region_z);
	}
	
	public IRegion getRegion(int x, int z) {
		// Get the index of this region
		final long index = get_index(x, z);
		
		IRegion region = regions.get(index);
		if(region == null) {
			synchronized(regions) {
				region = regions.get(index);
				if(region != null) return region;
				region = loadRegion(x, z);
				regions.put(index, region);
				return region;
			}
		} else if(region.getStatus() != Status.LOADED) {
			synchronized(region) {
				if(region.getStatus() == Status.LOADED
				|| region.getStatus() == Status.FAILED) return region;
				if(region instanceof Region) {
					try {
						((Region)region).loadRegion();
					} catch(IOException e) {
						LOGGER.error(e);
						e.printStackTrace();
					}
				}
				return region;
			}
		}
		
		return region;
	}
	
	@Deprecated
	public Collection<IRegion> getRegions() {
		return regions.values();
	}
	
	private IRegion loadRegion(int x, int z) {
		File file = new File(world.getFolder(), "region/r." + x + "." + z + ".mca");
		
		Region region = new Region(file, x, z);
		if(!file.exists()) {
			return region;
		}
		
		try {
			LOGGER.info("Loading region: { x: {}, z: {} }", x, z);
			region.loadRegion();
			return region;
		} catch(Exception e) {
			LOGGER.info("Failed to load region: { x: {}, z: {} }", x, z);
			LOGGER.error(e);
			e.printStackTrace();
			return region;
		}
	}
	
	public IChunk getChunk(int x, int z) {
		return getRegion(Math.floorDiv(x, 32), Math.floorDiv(z, 32))
			.getChunk(x & 31, z & 31);
	}
	
	public void unloadRegionsNotFound(Set<Long> set) {
		Iterator<Long> iter = regions.keySet().iterator();
		
		while(iter.hasNext()) {
			long index = iter.next();
			if(set.contains(index)) continue;
			
			IRegion region = regions.get(index);
			if(region instanceof Region) {
				// Unload chunks
				((Region)region).unloadRegion();
			} else {
				iter.remove();
			}
		}
	}
	
	private static long get_index(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
}
