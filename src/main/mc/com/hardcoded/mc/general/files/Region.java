package com.hardcoded.mc.general.files;

import java.io.File;
import java.io.IOException;

import com.hardcoded.api.Nonnull;
import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.RegionChunk;
import com.hardcoded.mc.general.world.RegionChunk.SubChunk;
import com.hardcoded.mc.general.world.RegionFile;

public class Region implements IRegion {
	private final File region_file;
	private final long region_index;
	private final int region_x;
	private final int region_z;
	private RegionFile region;
	private Status status;
	public IChunk[] chunks;
	
	public Region(File file, int x, int z) {
		this.region_file = file;
		this.region_x = x;
		this.region_z = z;
		this.region_index = ((long)(x) & 0xffffffffL) | (((long)z) << 32L);;
		this.status = Status.UNLOADED;
	}
	
	public boolean loadRegion() throws IOException {
		if(this.status != Status.UNLOADED) {
			return false;
		}
		
		if(!this.region_file.exists()) {
			this.status = Status.FAILED;
			return false;
		}
		
		try {
			this.status = Status.LOADING;
			this.region = new RegionFile(region_file);
			this.chunks = new IChunk[1024];
			this.status = Status.LOADED;
		} catch(Exception e) {
			this.status = Status.FAILED;
		}
		
		return true;
	}
	
	public void unloadRegion() {
		if(this.status == Status.UNLOADED) return;
		this.status = Status.UNLOADED;
		this.chunks = null;
		this.region = null;
	}
	
	@Override
	public int getX() {
		return region_x;
	}
	
	@Override
	public int getZ() {
		return region_z;
	}
	
	@Override
	public long getRegionIndex() {
		return region_index;
	}
	
	@Override
	public IChunk getChunk(int x, int z) {
		if(status != Status.LOADED) {
			return null;
		}

		IChunk chunk = chunks[x + z * 32];
		if(chunk == null) {
			chunk = chunks[x + z * 32] = readChunk(x, z);
		}
		
		return chunk;
	}
	
	@Override
	public boolean hasChunk(int x, int z) {
		return region.hasChunk(x, z);
	}
	
	@Override
	public Status getStatus() {
		return status;
	}
	
	private IChunk readChunk(int cx, int cz) {
		if(region.hasChunk(cx, cz)) {
			ByteBuf buf = region.getChunkBuffer(cx, cz);

			if(buf != null) {
				return createChunk(
					(32 * region_x) + cx,
					(32 * region_z) + cz,
					new RegionChunk(buf)
				);
			}
		}
		
		return new Chunk(cx, cz, Status.FAILED);
	}
	
	@Nonnull
	private static Chunk createChunk(int x, int z, RegionChunk rc) {
		Chunk chunk = new Chunk(x, z, Status.UNLOADED);
		chunk.biomeReader = rc.biomeReader;
		
		for(SubChunk sub : rc.getSubChunks()) {
			ChunkSection section = new ChunkSection(sub.y);
			chunk.sections.put(sub.y, section);
			section.blocks = new IBlockData[4096];
			System.arraycopy(sub.blocks, 0, section.blocks, 0, 4096);
			section.status = Status.LOADED;
		}
		
		chunk.status = Status.LOADED;
		return chunk;
	}
	
	@Override
	public String toString() {
		return String.format("%s@[%d,%d,%s]", Region.class.getName(), region_x, region_z, status);
	}
}
