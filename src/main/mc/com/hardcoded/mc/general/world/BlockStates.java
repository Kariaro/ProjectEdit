package com.hardcoded.mc.general.world;

import java.util.HashMap;
import java.util.Map;

public class BlockStates {
	private static final Map<Integer, BlockState> states = new HashMap<>();
	
	// Temporary
	public static IBlockState getState(String name) {
		int hash = name.hashCode();
		BlockState state = states.get(hash);
		if(state == null) {
			System.out.println(name);
			state = new BlockState(name);
			states.put(hash, state);
		}
		
		return state;
	}
	
	public static IBlockState getState(String name, int rgb) {
		int hash = name.hashCode();
		BlockState state = states.get(hash);
		if(state == null) {
			System.out.println(name);
			state = new BlockState(name).setColor(rgb);
			states.put(hash, state);
		}
		
		return state;
	}
}
