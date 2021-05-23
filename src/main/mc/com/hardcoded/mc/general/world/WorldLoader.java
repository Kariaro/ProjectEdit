package com.hardcoded.mc.general.world;

import java.io.File;
import java.io.IOException;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.RegionChunk.SubChunk;
import com.hardcoded.utils.NotNull;

public class WorldLoader {
	
	@NotNull
	public static IRegion loadRegion(World world, int x, int z) {
		File file = new File(world.getFile(), "region/r." + x + "." + z + ".mca");
		System.out.printf("Loading region: { x: %d, z: %d }\n", x, z);
		if(!file.exists()) {
			System.out.printf("Failed to load region: { x: %d, z: %d }\n", x, z);
			// Coulds not load the region file
			return Region.UNLOADED;
		}
		
		try {
			Region region = new Region();
			RegionFile rf = new RegionFile(file);
			for(int i = 0; i < 1024; i++) {
				int cx = (i & 31);
				int cz = (i / 32);
				if(rf.hasChunk(cx, cz)) {
					ByteBuf buf = rf.getChunkBuffer(cx, cz);
					
					if(buf != null) {
						if(!(x == -1 && z == 0)) {
							cx = 0;
							cz = 0;
						}
						//System.out.printf("    chunk: [ %d, %d ]\n", cx, cz);
						region.chunks[i] = createChunk(cx, cz, new RegionChunk(buf));
						continue;
					}
				}
				
				//System.out.printf("    chunk: [ %d, %d ]\n", cx, cz);
				region.chunks[i] = IChunk.UNLOADED;
			}
			
			return region;
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return IRegion.UNLOADED;
	}
	
	private static Chunk createChunk(int cx, int cz, RegionChunk rc) {
		Chunk chunk = new Chunk();
		
		boolean debug = (cx == 31 && cz == 0);
		for(int i = 0; i < 16; i++) {
			SubChunk sub = rc.getSubChunk(i);
			
			IChunkSection section = chunk.getSection(i);
			if(section instanceof ChunkSection) {
				ChunkSection sec = (ChunkSection)section;
				
				if(sub != null) {
					System.arraycopy(sub.blocks, 0, sec.blocks, 0, 4096);
				} else {
					for(int j = 0; j < 4096; j++) {
						sec.blocks[j] = Blocks.AIR;
					}
				}
			}
		}
		
		return chunk;
	}
}
