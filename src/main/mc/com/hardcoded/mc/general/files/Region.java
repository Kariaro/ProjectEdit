package com.hardcoded.mc.general.files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.world.RegionChunk;
import com.hardcoded.mc.general.world.RegionChunk.SubChunk;
import com.hardcoded.mc.general.world.RegionFile;
import com.hardcoded.utils.Nonnull;

public class Region implements IRegion {
	private RegionFile regionFile;
	private int region_x;
	private int region_z;
	
	public IChunk[] chunks = new IChunk[1024]; // 32x32
	
	public Region(File file, int x, int z) throws IOException {
		this.regionFile = new RegionFile(file);
		this.region_x = x;
		this.region_z = z;
	}
	
	@Override
	public IChunk getChunk(int x, int z) {
		IChunk chunk = chunks[x + z * 32];
		if(chunk == null) {
			return chunks[x + z * 32] = readChunk(x, z);
		}
		
		return chunk;
	}
	
	@Override
	public boolean hasChunk(int x, int z) {
		return regionFile.hasChunk(x, z);
//		return getChunk(x, z) != null;
	}
	
	private IChunk readChunk(int cx, int cz) {
		if(regionFile.hasChunk(cx, cz)) {
			ByteBuf buf = regionFile.getChunkBuffer(cx, cz);
			
			if(buf != null) {
				return createChunk(
					(32 * region_x) + cx,
					(32 * region_z) + cz,
					new RegionChunk(buf)
				);
			}
		}
		
		return IChunk.UNLOADED;
	}
	
	@Nonnull
	private static Chunk createChunk(int x, int z, RegionChunk rc) {
		Chunk chunk = new Chunk(x, z);
		
		for(int i = 0; i < 16; i++) {
			SubChunk sub = rc.getSubChunk(i);
			
			IChunkSection section = chunk.getSection(i);
			if(section instanceof ChunkSection) {
				ChunkSection sec = (ChunkSection)section;
				
				if(sub != null) {
					System.arraycopy(sub.blocks, 0, sec.blocks, 0, 4096);
				} else {
					Arrays.fill(sec.blocks, Blocks.AIR);
				}
			}
		}
		
		return chunk;
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
	public boolean isLoaded() {
		return true;
	}
}
