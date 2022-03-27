package com.hardcoded.mc.general.files;

import java.util.List;

import com.hardcoded.mc.general.world.BlockDataManager;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.util.Resource;

public class Blocks {
	public static IBlockData MISSING_BLOCK = get("projectedit:missing", true, 0x000000);
	
	public static final Resource AIR = Resource.of("minecraft:air");
	public static final Resource VOID_AIR = Resource.of("minecraft:void_air");
	public static final Resource CAVE_AIR = Resource.of("minecraft:cave_air");
	public static final Resource WATER = Resource.of("minecraft:water");
	public static final Resource LAVA = Resource.of("minecraft:lava");
	public static final Resource REDSTONE_WIRE = Resource.of("minecraft:redstone_wire");
	
	public static void init() {
		MISSING_BLOCK = get("projectedit:missing", true, 0x000000);
	}
	
	public static final IBlockData get(Resource id) {
		return BlockDataManager.getState(id.toString());
	}
	
	private static final IBlockData get(String name, boolean occluding, int rgb) {
		return BlockDataManager.addOrGetState(name, occluding, rgb, List.of());
	}
}
