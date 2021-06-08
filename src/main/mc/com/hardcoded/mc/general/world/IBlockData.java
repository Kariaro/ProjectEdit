package com.hardcoded.mc.general.world;

import java.util.Collection;
import java.util.Map;

import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;

public interface IBlockData {
	
	int getBlockId();
	
	String getName();
	
	boolean isAir();
	
	int getMapColor();
	
	Collection<IBlockState> getStates();
	
	IBlockData getFromStates(Map<String, String> states);
	
	IBlockStateList getStateList();
}
