package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;

public interface IChunk {
	public static IChunk UNLOADED = new IChunk() {
		@Override
		public IBlockData getBlock(int x, int y, int z) {
			return Blocks.VOID_AIR;
		}
		
		@Override
		public void setBlock(IBlockData state, int x, int y, int z) {
			
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
	
	IBlockData getBlock(int x, int y, int z);
	void setBlock(IBlockData state, int x, int y, int z);
	boolean isLoaded();
	
	/**
	 * Get the chunk section at the specified y coordinate
	 * @param y the
	 */
	IChunkSection getSection(int y);
}
