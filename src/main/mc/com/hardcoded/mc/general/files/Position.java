package com.hardcoded.mc.general.files;

public class Position {
	private int dimension;
	private long x;
	private long y;
	private long z;
	private float x_sub;
	private float y_sub;
	private float z_sub;
	
	public Position(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Position(double x, double y, double z) {
		this.x = (long)x;
		this.y = (long)y;
		this.z = (long)z;
		this.x_sub = (float)(x - this.x);
		this.y_sub = (float)(y - this.y);
		this.z_sub = (float)(z - this.z);
	}
	
	public int getDimension() {
		return dimension;
	}
	
	public int getBlockX() {
		return (int)x;
	}
	
	public int getBlockY() {
		return (int)y;
	}
	
	public int getBlockZ() {
		return (int)z;
	}
	
	public int getChunkX() {
		return Math.floorDiv((int)x, 16);
	}
	
	public int getChunkZ() {
		return Math.floorDiv((int)z, 16);
	}
	
	public long getChunkIndex() {
		return get_index(getChunkX(), getChunkZ());
	}
	
	public int getRegionX() {
		return Math.floorDiv((int)x, 512);
	}
	
	public int getRegionZ() {
		return Math.floorDiv((int)z, 512);
	}
	
	public long getRegionIndex() {
		return get_index(getRegionX(), getRegionZ());
	}
	
	public float getX() {
		return x + x_sub;
	}
	
	public float getY() {
		return y + y_sub;
	}
	
	public float getZ() {
		return z + z_sub;
	}
	
	public Position offset(double x, double y, double z) {
		return new Position(
			this.x + x_sub + x,
			this.y + y_sub + y,
			this.z + z_sub + z
		);
	}
	
	public static Position get(long x, long y, long z) {
		return new Position(x, y, z);
	}
	
	public static Position get(double x, double y, double z) {
		return new Position(x, y, z);
	}
	
	private static long get_index(int x, int y) {
		return ((long)(x) & 0xffffffffL) | (((long)y) << 32L);
	}
	
	@Override
	public String toString() {
		return String.format("Position[ x=%.4f, y=%.4f, z=%.4f ]", x + x_sub, y + y_sub, z + z_sub);
	}
}
