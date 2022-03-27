package com.hardcoded.mc.general.world;

import java.util.*;

import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;
import com.hardcoded.util.Resource;

public class BlockData implements IBlockData {
	protected final List<BlockData> children;
	protected final IBlockStateList stateList;
	protected final Resource resource;
//	protected final String namespace;
//	protected final String name;
	protected final int id;
	protected final IBlockData defaultState;
	
	/**
	 * This could change between runns
	 */
	protected final int internal_id;
	protected boolean is_opaque;
	protected boolean is_air;
	protected int rgb;
	
	public BlockData(String name, List<IBlockState> states) {
		this.defaultState = this;
		this.resource = Resource.of(name);
		this.id = name.hashCode();
		this.children = new ArrayList<>();
		this.stateList = new IBlockStateList();
		
		this.internal_id = BlockDataManager.id_states.size();
		BlockDataManager.id_states.put(this.internal_id, this);
		
		final int len = states.size();
		int[] statesLength = new int[len];
		for(int i = 0; i < len; i++) {
			IBlockState state = states.get(i);
			statesLength[i] = state.size();
			this.stateList.setState(state, state.getValues().get(0));
		}
		
		int[] index = new int[len];
		
		if(len > 0) {
			// Create child states
			
			boolean stop = false;
			while(true) {
				index[0]++;
				for(int i = 0; i < len; i++) {
					if(index[i] >= statesLength[i]) {
						if(i + 1 >= len) {
							stop = true;
							break;
						}
						
						index[i] = 0;
						index[i + 1]++;
						
						i--;
					}
				}
				
				if(stop) break;
				
				IBlockStateList list = new IBlockStateList();
				for(int i = 0; i < len; i++) {
					IBlockState state = states.get(i);
					list.setState(state, state.getValues().get(index[i]));
				}
				
				this.children.add(
					new BlockData(name, this, list)
					.setColor(rgb)
					.setOccluding(is_opaque)
				);
			}
		}
	}
	
	private BlockData(String name, IBlockData defaultState, IBlockStateList stateList) {
		this.defaultState = defaultState;
		this.resource = Resource.of(name);
		this.id = name.hashCode();
		this.children = List.of();
		this.stateList = stateList;
		
		this.internal_id = BlockDataManager.id_states.size();
		BlockDataManager.id_states.put(this.internal_id, this);
	}
	
	protected BlockData setColor(int rgb) {
		this.rgb = rgb;
		return this;
	}
	
	protected BlockData setOccluding(boolean occluding) {
		this.is_opaque = occluding;
		return this;
	}
	
	@Override
	public int getBlockId() {
		return id;
	}
	
	@Override
	public String getName() {
		return resource.path;
	}
	
	@Override
	public int getMapColor() {
		return rgb;
	}
	
	@Override
	public int getInternalId() {
		return internal_id;
	}
	
	@Override
	public Set<IBlockState> getStates() {
		return stateList.values();
	}
	
	public List<IBlockData> getChildren() {
		return new ArrayList<>(children);
	}
	
	@Override
	public boolean isOpaque() {
		return is_opaque;
	}
	
	@Override
	public IBlockData getFromStates(Map<String, String> states) {
		if(states.isEmpty()) return this;
		
		for(BlockData child : children) {
			if(child.stateList.matches(states))
				return child;
		}
		
		return this;
	}
	
	@Override
	public IBlockStateList getStateList() {
		return stateList;
	}
	
	@Override
	public IBlockData getDefaultState() {
		return defaultState;
	}
	
	@Override
	public boolean isAir() {
		return id == Blocks.get(Blocks.AIR).getBlockId()
			|| id == Blocks.get(Blocks.CAVE_AIR).getBlockId()
			|| id == Blocks.get(Blocks.VOID_AIR).getBlockId();
	}
	
	@Override
	public String toString() {
		if(stateList.isEmpty()) {
			return "IBlockData[%s]".formatted(resource);
		}
		
		return "IBlockData[%s] {%s}".formatted(resource, stateList);
	}
}
