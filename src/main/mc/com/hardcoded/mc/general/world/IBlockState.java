package com.hardcoded.mc.general.world;

public interface IBlockState {
	
	int getBlockId();
	
	String getName();
	
	boolean isAir();
	
	int getMapColor();
}
