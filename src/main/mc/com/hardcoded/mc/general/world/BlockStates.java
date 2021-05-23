package com.hardcoded.mc.general.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockStates {
	private static final Map<Integer, BlockState> states = new HashMap<>();
	
	// Temporary
	public static IBlockState getState(String name) {
		return getState(name, 0);
	}
	
	public static IBlockState getState(String name, int rgb) {
		int hash = name.hashCode();
		BlockState state = states.get(hash);
		if(state == null) {
			state = new BlockState(name).setColor(rgb);
			states.put(hash, state);
		}
		
		return state;
	}
	
	public static final Set<IBlockState> getStates() {
		return states.values().stream().map(i -> (IBlockState)i).collect(Collectors.toUnmodifiableSet());
	}
}
