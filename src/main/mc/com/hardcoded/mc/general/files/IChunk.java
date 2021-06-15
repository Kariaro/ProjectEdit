package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;

public interface IChunk {
	public enum Status {
		UNLOADED,
		LOADING,
		LOADED,
	}
	
	@Deprecated
	/**
	 * Replace this with a Chunk with the unloaded status
	 */
	public static IChunk UNLOADED = new IChunk() {
		@Override
		public IBlockData getBlock(int x, int y, int z) {
			return Blocks.VOID_AIR;
		}
		
		@Override
		public void setBlock(IBlockData state, int x, int y, int z) {
			
		}
		
		@Override
		public int getX() {
			return 0;
		}
		
		@Override
		public int getZ() {
			return 0;
		}
		
		@Override
		public boolean isDirty() {
			return false;
		}
		
		@Override
		public World getWorld() {
			return null;
		}
		
		@Override
		public IChunkSection getSection(int y) {
			return IChunkSection.UNLOADED;
		}
		
		@Override
		public boolean isLoaded() {
			return false;
		}
	};
	
	World getWorld();
	
	IBlockData getBlock(int x, int y, int z);
	
	void setBlock(IBlockData state, int x, int y, int z);
	
	boolean isLoaded();
	
	boolean isDirty();
	
	int getX();
	int getZ();
	
	/**
	 * Get the chunk section at the specified y coordinate
	 * @param y the
	 */
	IChunkSection getSection(int y);
}
