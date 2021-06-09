package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockData;

public interface IChunkSection {
	public static final IChunkSection UNLOADED = new IChunkSection() {
		@Override
		public IBlockData getBlock(int x, int y, int z) {
			return Blocks.AIR;
		}
		
		@Override
		public void setBlock(IBlockData state, int x, int y, int z) {
			
		}
		
		@Override
		public boolean isLoaded() {
			return false;
		}
	};
	
	IBlockData getBlock(int x, int y, int z);
	
	void setBlock(IBlockData state, int x, int y, int z);
	
	boolean isLoaded();
}
