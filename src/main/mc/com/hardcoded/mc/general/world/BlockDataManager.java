package com.hardcoded.mc.general.world;

import java.util.*;
import java.util.stream.Collectors;

public class BlockDataManager {
	private static final Map<Integer, BlockData> states = new HashMap<>();
	
	// Temporary
	public static IBlockData getState(String name) {
		return states.get(name.hashCode());
	}
	
	public static IBlockData getState(String name, Map<String, String> states) {
		IBlockData data = getState(name);
		if(data == null) return null;
		return data.getFromStates(states);
	}
	
	public static IBlockData getState(String name, int rgb, List<IBlockState> list) {
		int hash = name.hashCode();
		BlockData state = states.get(hash);
		if(state == null) {
			state = new BlockData(name, list).setColor(rgb);
			states.put(hash, state);
		}
		
		return state;
	}
	
	public static final Set<IBlockData> getStates() {
		return states.values().stream().map(i -> (IBlockData)i).collect(Collectors.toUnmodifiableSet());
	}
}
