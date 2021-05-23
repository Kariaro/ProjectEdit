package com.hardcoded.mc.general.files;

public class BlockPosition {
	public int x;
	public int y;
	public int z;
	
	public BlockPosition() {
		
	}
	
	public BlockPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public void setXYZ(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public int getChunkX() {
		return Math.floorDiv(x, 16);
	}
	
	public int getChunkZ() {
		return Math.floorDiv(z, 16);
	}
	
	
	public int getRegionX() {
		return Math.floorDiv(x, 512);
	}
	
	public int getRegionZ() {
		return Math.floorDiv(z, 512);
	}
	
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	
	public int getLocalChunkX() {
		return x & 15;
	}
	
	public int getLocalChunkY() {
		return y;
	}
	
	public int getLocalChunkZ() {
		return z & 15;
	}
	
	public static BlockPosition get(int x, int y, int z) {
		return new BlockPosition(x, y, z);
	}
	
	@Override
	public String toString() {
		return String.format("BlockPosition { x: %d, y: %d, z: %d }", x, y, z);
	}
}
