package com.hardcoded.mc.general.files;

public interface IRegion {
	enum Status {
		LOADED,
		FAILED,
		LOADING,
		UNLOADED
	}
	
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
		
		@Override
		public int getX() {
			return 0;
		}
		
		@Override
		public int getZ() {
			return 0;
		}
	};
	
	/**
	 * Return this if the region failed to load
	 */
	public static final IRegion FAILED = new IRegion() {
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
			return true;
		}
		
		@Override
		public int getX() {
			return 0;
		}
		
		@Override
		public int getZ() {
			return 0;
		}
	};
	
	boolean isLoaded();
	
	IChunk getChunk(int x, int z);
	
	boolean hasChunk(int x, int z);
	
	int getX();
	
	int getZ();
}
