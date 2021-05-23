package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockState;

public interface IChunk {
	public static IChunk UNLOADED = new IChunk() {
		@Override
		public IBlockState getBlock(int x, int y, int z) {
			return Blocks.AIR;
		}
		
		@Override
		public void setBlock(IBlockState state, int x, int y, int z) {
			
		}
		
		@Override
		public boolean isLoaded() {
			return false;
		}
	};
	
	IBlockState getBlock(int x, int y, int z);
	
	void setBlock(IBlockState state, int x, int y, int z);
	boolean isLoaded();
}
