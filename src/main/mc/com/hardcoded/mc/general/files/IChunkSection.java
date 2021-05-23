package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.world.IBlockState;

public interface IChunkSection {
	public static final IChunkSection UNLOADED = new IChunkSection() {
		@Override
		public IBlockState getBlock(int x, int y, int z) {
			return Blocks.AIR;
		}
		
		@Override
		public void setBlock(IBlockState state, int x, int y, int z) {
			
		}
	};
	
	IBlockState getBlock(int x, int y, int z);
	
	void setBlock(IBlockState state, int x, int y, int z);
}
