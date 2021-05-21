package com.hardcoded.lwjgl;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.RegionFile;
import com.hardcoded.mc.general.nbt.NBTBase;
import com.hardcoded.mc.general.nbt.NBTTagCompound;

// This class is just debugging the world files
public class WorldReader {
	public WorldReader() {
		
	}
	
	private byte[] readFile(File file) throws Exception {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		
		try(GZIPInputStream stream = new GZIPInputStream(new FileInputStream(file))) {
			byte[] buffer = new byte[65536];
			
			while(stream.available() != 0) {
				int readBytes = stream.read(buffer);
				if(readBytes > 0)
					bs.write(buffer, 0, readBytes);
			}
		} catch(IOException e) {
			throw e;
		}
		
		return bs.toByteArray();
	}
	
	public void read(File file) throws Exception {
		byte[] bytes = readFile(file);
		ByteBuf buf = new ByteBuf(bytes);
		
		NBTTagCompound nbt = NBTBase.readNBTTagCompound(buf);
		System.out.println(nbt);
	}
	
	public RegionFile region;
	public void readTest(File file) throws Exception {
		this.region = new RegionFile(file);
	}
	
	private File cache;
	public File getFolder() {
		if(cache != null) return cache;
		File[] files = Minecraft.getSaves();
		if(files.length > 0) {
			File save = files[0];
			cache = save;
		}
		
		return cache;
	}
	
	private Map<Long, RegionFile> map = new HashMap<>();
	public RegionFile tryLoad(int x, int z) {
		long index = ((long)(x) & 0xffffffffL) | (((long)z) << 32L);
		{
			RegionFile region = map.get(index);
			if(region != null) return region;
		}
		
		File file = getFolder();
		file = new File(file, "region/r." + x + "." + z + ".mca");
		if(!file.exists()) {
			return null;
		}
		
		try {
			RegionFile region = new RegionFile(file);
			map.put(index, region);
			
			return region;
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
