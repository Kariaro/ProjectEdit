package com.hardcoded.mc.general.world;

import java.util.*;

import org.joml.Matrix4f;

import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;
import com.hardcoded.utils.FastModelJsonLoader.FastModel;

public class BlockData implements IBlockData {
	protected final List<BlockData> children;
	protected final IBlockStateList stateList;
	protected final String namespace;
	protected final String name;
	protected final int id;
	protected int rgb;
	
	public Matrix4f model2_transform = new Matrix4f();
	public FastModel.ModelObject model2;
	
	public BlockData(String name, List<IBlockState> states) {
		this.namespace = name.substring(0, name.indexOf(':'));
		this.name = name.substring(name.indexOf(':') + 1);
		this.id = name.hashCode();
		this.children = new ArrayList<>();
		this.stateList = new IBlockStateList();
		
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
				
				this.children.add(new BlockData(name, list));
			}
		}
	}
	
	public BlockData(String name, IBlockStateList stateList) {
		this.namespace = name.substring(0, name.indexOf(':'));
		this.name = name.substring(name.indexOf(':') + 1);
		this.id = name.hashCode();
		this.children = List.of();
		this.stateList = stateList;
	}
	
	protected BlockData setColor(int rgb) {
		this.rgb = rgb;
		return this;
	}
	
	@Override
	public int getBlockId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getMapColor() {
		return rgb;
	}
	
	@Override
	public Collection<IBlockState> getStates() {
		return stateList.values();
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
	public boolean isAir() {
		return id == Blocks.AIR.getBlockId()
			|| id == Blocks.CAVE_AIR.getBlockId()
			|| id == Blocks.VOID_AIR.getBlockId();
	}
	
	@Override
	public String toString() {
		if(children.isEmpty()) {
			return "IBlockData[" + namespace + ":" + name + "]";
		}
		
		return "IBlockData[" + namespace + ":" + name + "] {" + stateList + "}";
	}
}
