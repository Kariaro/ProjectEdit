package com.hardcoded.mc.general.world;

import java.util.HashMap;
import java.util.Map;

public class Biomes {
	private static final Map<String, Biome> biomes = new HashMap<>();
	private static final Map<Integer, Biome> idToBiome = new HashMap<>();
	
	public static final Biome BADLANDS = get("minecraft:badlands", 37, 2.0, 0.0);
	public static final Biome BADLANDS_PLATEAU = get("minecraft:badlands_plateau", 39, 2.0, 0.0);
	public static final Biome BAMBOO_JUNGLE = get("minecraft:bamboo_jungle", 168, 0.9, 0.9);
	public static final Biome BAMBOO_JUNGLE_HILLS = get("minecraft:bamboo_jungle_hills", 169, 0.9, 0.9);
	public static final Biome BASALT_DELTAS = get("minecraft:basalt_deltas", 173, 2.0, 0.0);
	public static final Biome BEACH = get("minecraft:beach", 16, 0.8, 0.4);
	public static final Biome BIRCH_FOREST = get("minecraft:birch_forest", 27, 0.6, 0.6);
	public static final Biome BIRCH_FOREST_HILLS = get("minecraft:birch_forest_hills", 28, 0.6, 0.6);
	public static final Biome COLD_OCEAN = get("minecraft:cold_ocean", 46, 0.5, 0.5);
	public static final Biome CRIMSON_FOREST = get("minecraft:crimson_forest", 171, 2.0, 0.0);
	public static final Biome DARK_FOREST = get("minecraft:dark_forest", 29, 0.7, 0.8);
	public static final Biome DARK_FOREST_HILLS = get("minecraft:dark_forest_hills", 157, 0.7, 0.8);
	public static final Biome DEEP_COLD_OCEAN = get("minecraft:deep_cold_ocean", 49, 0.5, 0.5);
	public static final Biome DEEP_FROZEN_OCEAN = get("minecraft:deep_frozen_ocean", 50, 0.5, 0.5);
	public static final Biome DEEP_LUKEWARM_OCEAN = get("minecraft:deep_lukewarm_ocean", 48, 0.5, 0.5);
	public static final Biome DEEP_OCEAN = get("minecraft:deep_ocean", 24, 0.5, 0.5);
	public static final Biome DEEP_WARM_OCEAN = get("minecraft:deep_warm_ocean", 47, 0.5, 0.5);
	public static final Biome DESERT = get("minecraft:desert", 2, 2.0, 0.0);
	public static final Biome DESERT_HILLS = get("minecraft:desert_hills", 17, 2.0, 0.0);
	public static final Biome DESERT_LAKES = get("minecraft:desert_lakes", 130, 2.0, 0.0);
	public static final Biome END_BARRENS = get("minecraft:end_barrens", 43, 0.5, 0.5);
	public static final Biome END_HIGHLANDS = get("minecraft:end_highlands", 42, 0.5, 0.5);
	public static final Biome END_MIDLANDS = get("minecraft:end_midlands", 41, 0.5, 0.5);
	public static final Biome ERODED_BADLANDS = get("minecraft:eroded_badlands", 165, 2.0, 0.0);
	public static final Biome FLOWER_FOREST = get("minecraft:flower_forest", 132, 0.7, 0.8);
	public static final Biome FOREST = get("minecraft:forest", 4, 0.7, 0.8);
	public static final Biome FROZEN_OCEAN = get("minecraft:frozen_ocean", 10, 0.0, 0.5);
	public static final Biome FROZEN_RIVER = get("minecraft:frozen_river", 11, 0.0, 0.5);
	public static final Biome GIANT_SPRUCE_TAIGA = get("minecraft:giant_spruce_taiga", 160, 0.3, 0.8);
	public static final Biome GIANT_SPRUCE_TAIGA_HILLS = get("minecraft:giant_spruce_taiga_hills", 161, 0.3, 0.8);
	public static final Biome GIANT_TREE_TAIGA = get("minecraft:giant_tree_taiga", 32, 0.3, 0.8);
	public static final Biome GIANT_TREE_TAIGA_HILLS = get("minecraft:giant_tree_taiga_hills", 33, 0.3, 0.8);
	public static final Biome GRAVELLY_MOUNTAINS = get("minecraft:gravelly_mountains", 131, 0.2, 0.3);
	public static final Biome ICE_SPIKES = get("minecraft:ice_spikes", 140, 0.0, 0.5);
	public static final Biome JUNGLE = get("minecraft:jungle", 21, 0.9, 0.9);
	public static final Biome JUNGLE_EDGE = get("minecraft:jungle_edge", 23, 0.9, 0.8);
	public static final Biome JUNGLE_HILLS = get("minecraft:jungle_hills", 22, 0.9, 0.9);
	public static final Biome LUKEWARM_OCEAN = get("minecraft:lukewarm_ocean", 45, 0.5, 0.5);
	public static final Biome MODIFIED_BADLANDS_PLATEAU = get("minecraft:modified_badlands_plateau", 167, 2.0, 0.0);
	public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = get("minecraft:modified_gravelly_mountains", 162, 0.2, 0.3);
	public static final Biome MODIFIED_JUNGLE = get("minecraft:modified_jungle", 149, 0.9, 0.9);
	public static final Biome MODIFIED_JUNGLE_EDGE = get("minecraft:modified_jungle_edge", 151, 0.9, 0.8);
	public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = get("minecraft:modified_wooded_badlands_plateau", 166, 2.0, 0.0);
	public static final Biome MOUNTAIN_EDGE = get("minecraft:mountain_edge", 20, 0.2, 0.3);
	public static final Biome MOUNTAINS = get("minecraft:mountains", 3, 0.2, 0.3);
	public static final Biome MUSHROOM_FIELD_SHORE = get("minecraft:mushroom_field_shore", 15, 0.9, 1.0);
	public static final Biome MUSHROOM_FIELDS = get("minecraft:mushroom_fields", 14, 0.9, 1.0);
	public static final Biome NETHER_WASTES = get("minecraft:nether_wastes", 8, 2.0, 0.0);
	public static final Biome OCEAN = get("minecraft:ocean", 0, 0.5, 0.5);
	public static final Biome PLAINS = get("minecraft:plains", 1, 0.8, 0.4);
	public static final Biome RIVER = get("minecraft:river", 7, 0.5, 0.5);
	public static final Biome SAVANNA = get("minecraft:savanna", 35, 1.2, 0.0);
	public static final Biome SAVANNA_PLATEAU = get("minecraft:savanna_plateau", 36, 1.0, 0.0);
	public static final Biome SHATTERED_SAVANNA = get("minecraft:shattered_savanna", 163, 1.1, 0.0);
	public static final Biome SHATTERED_SAVANNA_PLATEAU = get("minecraft:shattered_savanna_plateau", 164, 1.0, 0.0);
	public static final Biome SMALL_END_ISLANDS = get("minecraft:small_end_islands", 40, 0.5, 0.5);
	public static final Biome SNOWY_BEACH = get("minecraft:snowy_beach", 26, 0.1, 0.3);
	public static final Biome SNOWY_MOUNTAINS = get("minecraft:snowy_mountains", 13, 0.0, 0.5);
	public static final Biome SNOWY_TAIGA = get("minecraft:snowy_taiga", 30, -0.5, 0.4);
	public static final Biome SNOWY_TAIGA_HILLS = get("minecraft:snowy_taiga_hills", 31, -0.5, 0.4);
	public static final Biome SNOWY_TAIGA_MOUNTAINS = get("minecraft:snowy_taiga_mountains", 158, -0.5, 0.4);
	public static final Biome SNOWY_TUNDRA = get("minecraft:snowy_tundra", 12, 0.0, 0.5);
	public static final Biome SOUL_SAND_VALLEY = get("minecraft:soul_sand_valley", 170, 2.0, 0.0);
	public static final Biome STONE_SHORE = get("minecraft:stone_shore", 25, 0.2, 0.3);
	public static final Biome SUNFLOWER_PLAINS = get("minecraft:sunflower_plains", 129, 0.8, 0.4);
	public static final Biome SWAMP = get("minecraft:swamp", 6, 0.8, 0.9);
	public static final Biome SWAMP_HILLS = get("minecraft:swamp_hills", 134, 0.8, 0.9);
	public static final Biome TAIGA = get("minecraft:taiga", 5, 0.3, 0.8);
	public static final Biome TAIGA_HILLS = get("minecraft:taiga_hills", 19, 0.3, 0.8);
	public static final Biome TAIGA_MOUNTAINS = get("minecraft:taiga_mountains", 133, 0.3, 0.8);
	public static final Biome TALL_BIRCH_FOREST = get("minecraft:tall_birch_forest", 155, 0.6, 0.6);
	public static final Biome TALL_BIRCH_HILLS = get("minecraft:tall_birch_hills", 156, 0.6, 0.6);
	public static final Biome THE_END = get("minecraft:the_end", 9, 0.5, 0.5);
	public static final Biome THE_VOID = get("minecraft:the_void", 127, 0.5, 0.5);
	public static final Biome WARM_OCEAN = get("minecraft:warm_ocean", 44, 0.5, 0.5);
	public static final Biome WARPED_FOREST = get("minecraft:warped_forest", 172, 2.0, 0.0);
	public static final Biome WOODED_BADLANDS_PLATEAU = get("minecraft:wooded_badlands_plateau", 38, 2.0, 0.0);
	public static final Biome WOODED_HILLS = get("minecraft:wooded_hills", 18, 0.7, 0.8);
	public static final Biome WOODED_MOUNTAINS = get("minecraft:wooded_mountains", 34, 0.2, 0.3);
	
	private static Biome get(String name, int id, double temperature, double downfall) {
		Biome biome = new Biome(name, temperature, downfall);
		biomes.put(name, biome);
		idToBiome.put(id, biome);
		return biome;
	}
	
	public static Biome getFromName(String name) {
		return biomes.get(name);
	}
	
	public static Biome getFromId(int id) {
		return idToBiome.get(id);
	}
}
