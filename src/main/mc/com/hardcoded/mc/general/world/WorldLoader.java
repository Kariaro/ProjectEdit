package com.hardcoded.mc.general.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.nbt.NBTBase;
import com.hardcoded.mc.general.nbt.NBTTagCompound;
import com.hardcoded.mc.general.world.RegionChunk.SubChunk;
import com.hardcoded.utils.NotNull;
import com.hardcoded.utils.StreamUtils;

public class WorldLoader {
	private static final Logger LOGGER = LogManager.getLogger(WorldLoader.class);
	
	@NotNull
	public static IRegion loadRegion(World world, int x, int z) {
		File file = new File(world.getFile(), "region/r." + x + "." + z + ".mca");
		LOGGER.info("Loading region: { x: {}, z: {} }", x, z);
		
		if(!file.exists()) {
			LOGGER.info("Failed to load region: { x: {}, z: {} }", x, z);
			return Region.UNLOADED;
		}
		
		try {
			Region region = new Region(file, x, z);
//			RegionFile rf = new RegionFile(file);
//			for(int i = 0; i < 1024; i++) {
//				int cx = (i & 31);
//				int cz = (i / 32);
//				if(rf.hasChunk(cx, cz)) {
//					ByteBuf buf = rf.getChunkBuffer(cx, cz);
//					
//					if(buf != null) {
//						region.chunks[i] = createChunk((32 * x) + cx, (32 * z) + cz,  new RegionChunk(buf));
//						continue;
//					}
//				}
//				
//				region.chunks[i] = IChunk.UNLOADED;
//			}
			
			return region;
		} catch(IOException e) {
			//e.printStackTrace();
			// TODO: Fix chunk loading errors
			
		}
		
		return IRegion.UNLOADED;
	}
	
	@NotNull
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
	
	@NotNull
	public static NBTTagCompound loadLevelDat(World world) {
		File file = new File(world.getFile(), "level.dat");
		
		if(!file.exists()) {
			return new NBTTagCompound();
		}
		
		try(FileInputStream stream = new FileInputStream(file)) {
			return NBTBase.readNBTTagCompound(ByteBuf.readOnly(StreamUtils.decompress_gzip(stream.readAllBytes())));
		} catch(IOException e) {
			LOGGER.error(e);
		}
		
		return new NBTTagCompound();
	}
}
