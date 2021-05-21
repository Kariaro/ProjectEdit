package com.hardcoded.mc.general.files;

import java.io.File;

/**
 * This file is one of {@code r.a.b.mc[ar]}
 * 
 * @author HardCoded
 */
public class RegionData {
	private File file;
	private final int x;
	private final int y;
	
	public RegionData(String path) {
		this.file = new File(path);
		
		String[] parts = file.getName().split(".");
		this.x = Integer.valueOf(parts[1]);
		this.y = Integer.valueOf(parts[2]);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return String.format("RegionData [ x: %d, y: %d ]", x, y);
	}
}
