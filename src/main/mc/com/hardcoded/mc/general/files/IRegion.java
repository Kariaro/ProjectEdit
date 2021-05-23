package com.hardcoded.mc.general.files;

public interface IRegion {
	public static final IRegion UNLOADED = new IRegion() {
		@Override
		public IChunk getChunk(int x, int z) {
			return IChunk.UNLOADED;
		}
		
		@Override
		public boolean hasChunk(int x, int z) {
			return false;
		}
		
		@Override
		public boolean isLoaded() {
			return false;
		}
	};
	
	boolean isLoaded();
	
	IChunk getChunk(int x, int z);
	
	boolean hasChunk(int x, int z);
}
