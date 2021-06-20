package com.hardcoded.mc.general.world;

import java.util.*;

import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.IBlockState.IBlockStateList;
import com.hardcoded.utils.FastModelJsonLoader.FastModel;

public class BlockData implements IBlockData {
	protected final List<BlockData> children;
	protected final IBlockStateList stateList;
	protected final String namespace;
	protected final String name;
	protected final int id;
	protected final IBlockData defaultState;
	
	/**
	 * This could change between runns
	 */
	protected final int internal_id;
	protected boolean occluding;
	protected int rgb;
	
	public List<FastModel.ModelObject> model_objects = new ArrayList<>();
	
	public BlockData(String name, List<IBlockState> states) {
		this.defaultState = this;
		this.namespace = name.substring(0, name.indexOf(':'));
		this.name = name.substring(name.indexOf(':') + 1);
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
					.setOccluding(occluding)
				);
			}
		}
	}
	
	private BlockData(String name, IBlockData defaultState, IBlockStateList stateList) {
		this.defaultState = defaultState;
		this.namespace = name.substring(0, name.indexOf(':'));
		this.name = name.substring(name.indexOf(':') + 1);
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
		this.occluding = occluding;
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
		return occluding
			|| id == Blocks.WATER.getBlockId()
			|| id == Blocks.LAVA.getBlockId()
			
			|| id == Blocks.GRASS.getBlockId()
			|| id == Blocks.TALL_GRASS.getBlockId()
			|| id == Blocks.FERN.getBlockId()
			|| id == Blocks.LARGE_FERN.getBlockId()
			
			|| id == Blocks.CACTUS.getBlockId()
			
			|| id == Blocks.HOPPER.getBlockId()
			|| id == Blocks.GRINDSTONE.getBlockId()
			|| id == Blocks.LANTERN.getBlockId()
			
			|| id == Blocks.SPRUCE_TRAPDOOR.getBlockId()
			|| id == Blocks.OAK_TRAPDOOR.getBlockId()
			
			|| id == Blocks.OAK_DOOR.getBlockId()
			|| id == Blocks.IRON_DOOR.getBlockId()
			|| id == Blocks.CHEST.getBlockId()
			
			|| id == Blocks.WHEAT.getBlockId()
			|| id == Blocks.BEETROOTS.getBlockId()
			|| id == Blocks.POTATOES.getBlockId()
			|| id == Blocks.CARROTS.getBlockId()
			
			|| id == Blocks.STONE_PRESSURE_PLATE.getBlockId()
			
			|| id == Blocks.GLASS_PANE.getBlockId()
			|| id == Blocks.IRON_BARS.getBlockId()
			
			|| id == Blocks.TORCH.getBlockId()
			
			|| id == Blocks.BLUE_ORCHID.getBlockId()
			|| id == Blocks.OXEYE_DAISY.getBlockId()
			|| id == Blocks.AZURE_BLUET.getBlockId()
			|| id == Blocks.SUNFLOWER.getBlockId()
			|| id == Blocks.ALLIUM.getBlockId()
			|| id == Blocks.DANDELION.getBlockId()
			|| id == Blocks.POPPY.getBlockId()
			|| id == Blocks.DEAD_BUSH.getBlockId()
			|| id == Blocks.ORANGE_TULIP.getBlockId()
			|| id == Blocks.ROSE_BUSH.getBlockId()
			|| id == Blocks.SWEET_BERRY_BUSH.getBlockId()
			|| id == Blocks.CORNFLOWER.getBlockId()
			|| id == Blocks.AZURE_BLUET.getBlockId()
			|| id == Blocks.LILAC.getBlockId()
			|| id == Blocks.PEONY.getBlockId()
			|| id == Blocks.LILY_OF_THE_VALLEY.getBlockId()
			|| id == Blocks.KELP_PLANT.getBlockId()
			
			|| id == Blocks.ACACIA_FENCE.getBlockId()
			|| id == Blocks.ACACIA_FENCE_GATE.getBlockId()
			|| id == Blocks.BIRCH_FENCE.getBlockId()
			|| id == Blocks.BIRCH_FENCE_GATE.getBlockId()
			|| id == Blocks.CRIMSON_FENCE.getBlockId()
			|| id == Blocks.CRIMSON_FENCE_GATE.getBlockId()
			|| id == Blocks.DARK_OAK_FENCE.getBlockId()
			|| id == Blocks.DARK_OAK_FENCE_GATE.getBlockId()
			|| id == Blocks.JUNGLE_FENCE.getBlockId()
			|| id == Blocks.JUNGLE_FENCE_GATE.getBlockId()
			|| id == Blocks.NETHER_BRICK_FENCE.getBlockId()
			|| id == Blocks.OAK_FENCE.getBlockId()
			|| id == Blocks.OAK_FENCE_GATE.getBlockId()
			|| id == Blocks.SPRUCE_FENCE.getBlockId()
			|| id == Blocks.SPRUCE_FENCE_GATE.getBlockId()
			|| id == Blocks.WARPED_FENCE.getBlockId()
			|| id == Blocks.WARPED_FENCE_GATE.getBlockId()
			
			|| id == Blocks.STONE_BRICK_WALL.getBlockId()
			|| id == Blocks.COBBLESTONE_WALL.getBlockId()
			|| id == Blocks.MOSSY_STONE_BRICK_WALL.getBlockId()
			
			|| id == Blocks.LADDER.getBlockId()
			
			|| id == Blocks.FIRE.getBlockId()
			
			|| id == Blocks.SNOW.getBlockId()
			|| id == Blocks.VINE.getBlockId()
			|| id == Blocks.SUGAR_CANE.getBlockId()
			
			|| id == Blocks.SEAGRASS.getBlockId()
			|| id == Blocks.SEA_PICKLE.getBlockId()
			|| id == Blocks.TALL_SEAGRASS.getBlockId()
			|| id == Blocks.KELP_PLANT.getBlockId()
			
			|| id == Blocks.POWERED_RAIL.getBlockId()
			|| id == Blocks.ACTIVATOR_RAIL.getBlockId()
			|| id == Blocks.RAIL.getBlockId()
			|| id == Blocks.DETECTOR_RAIL.getBlockId()
			
			|| id == Blocks.TORCH.getBlockId()
			|| id == Blocks.SOUL_TORCH.getBlockId()
			
			|| id == Blocks.SPAWNER.getBlockId()
			
			|| id == Blocks.COBWEB.getBlockId()
			
//			|| id == Blocks.ACACIA_LEAVES.getBlockId()
//			|| id == Blocks.BIRCH_LEAVES.getBlockId()
//			|| id == Blocks.DARK_OAK_LEAVES.getBlockId()
//			|| id == Blocks.JUNGLE_LEAVES.getBlockId()
//			|| id == Blocks.OAK_LEAVES.getBlockId()
//			|| id == Blocks.SPRUCE_LEAVES.getBlockId()
		;
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
		return id == Blocks.AIR.getBlockId()
			|| id == Blocks.CAVE_AIR.getBlockId()
			|| id == Blocks.VOID_AIR.getBlockId();
	}
	
	@Override
	public String toString() {
		if(stateList.isEmpty()) {
			return "IBlockData[" + namespace + ":" + name + "]";
		}
		
		return "IBlockData[" + namespace + ":" + name + "] {" + stateList + "}";
	}
}
