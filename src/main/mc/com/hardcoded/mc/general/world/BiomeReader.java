package com.hardcoded.mc.general.world;

import java.util.Arrays;

public class BiomeReader {
	private static final int WIDTH_BITS = 2;
	private static final int HEIGHT_BITS = 6;
	public static final int BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
	public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
	public static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;
	
	public static void main(String[] args) {
		System.out.println(WIDTH_BITS);
		System.out.println(HEIGHT_BITS);
		
	}
	public Biome[] biomes;
	protected BiomeReader(int[] array) {
		this.biomes = new Biome[BIOMES_SIZE];
		Arrays.fill(biomes, Biomes.PLAINS);
		
		for(int i = 0; i < array.length; ++i) {
			Biome biome = Biomes.getFromId(array[i]);
			biomes[i] = biome;
			if(biome == null) {
				biomes[i] = Biomes.PLAINS;
			}
		}
	}
	
	public Biome getBiome(int x, int y, int z) {
		int nx = x & HORIZONTAL_MASK;
		int ny = y;
		if(ny < 0) ny = 0;
		if(ny > VERTICAL_MASK) ny = VERTICAL_MASK;
		int nz = z & HORIZONTAL_MASK;
		return this.biomes[ny << WIDTH_BITS + WIDTH_BITS | nz << WIDTH_BITS | nx];
	}
}
