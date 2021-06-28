package com.hardcoded.render.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.hardcoded.mc.constants.Direction;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.BlockDataManager;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;

/**
 * Utility class for rescaling and simplifying meshes
 */
public class LodUtils {
	public static IBlockData getCommonBlock(IChunkSection section, int x, int y, int z, int size) {
		if(!section.isLoaded()) {
			return Blocks.VOID_AIR;
		}
		
		if(size == 1) {
			return section.getBlock(x, y, z);
		}
		
		int sizeq = size * size;
		int vol = sizeq * size;
		
		Map<Integer, Integer> map = new HashMap<>();
		
		for(int i = 0; i < vol; i++) {
			int xp = i % size;
			int zp = (i / size) % size;
			int yp = (i / sizeq);
			
			IBlockData state = section.getBlock(x + xp, y + yp, z + zp);
			if(state.isAir()) continue;
			
			int id = state.getInternalId();
			Integer count = map.get(id);
			map.put(id, count != null ? count+1:1);
		}
		
		if(map.isEmpty()) {
			return Blocks.AIR;
		}
		
		int id = -1;
		int value = -1;
		for(Entry<Integer, Integer> e : map.entrySet()) {
			int v = e.getValue();
			if(v > value) {
				id = e.getKey();
				value = v;
			}
		}
		
		if(id < 0) return Blocks.AIR;
		
		IBlockData data = BlockDataManager.getStateFromInternalId(id);
		
		if(data != null) {
			return data;
		}
		
		return Blocks.AIR;
	}
	
	public static IBlockData getCommonBlock(World world, int x, int y, int z, int size) {
		IChunk chunk = world.getChunk(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
		if(chunk.getStatus() != Status.LOADED) {
			return Blocks.VOID_AIR;
		}
		
		return getCommonBlock(chunk.getSection(y / 16), x & 15, y & 15, z & 15, size);
	}
	
	public static int getShownFaces(World world, int x, int y, int z, int size) {
		// When this method is called xyz should be aligned with size.
		final int o = size;
		
		return (getCommonBlock(world, x + o, y    , z    , size).isOpaque() ? Direction.FACE_RIGHT:0)
			 | (getCommonBlock(world, x - o, y    , z    , size).isOpaque() ? Direction.FACE_LEFT:0)
			 | (getCommonBlock(world, x    , y + o, z    , size).isOpaque() ? Direction.FACE_UP:0)
			 | (getCommonBlock(world, x    , y - o, z    , size).isOpaque() ? Direction.FACE_DOWN:0)
			 | (getCommonBlock(world, x    , y    , z + o, size).isOpaque() ? Direction.FACE_FRONT:0)
			 | (getCommonBlock(world, x    , y    , z - o, size).isOpaque() ? Direction.FACE_BACK:0);
	}
}
