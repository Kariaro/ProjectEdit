package com.hardcoded.mc.general.nbt;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.hardcoded.mc.general.ByteBuf;

/**
 * @author HardCoded
 */
public class NBTTagCompound extends NBTBase {
	private Map<String, NBTBase> map = new LinkedHashMap<>();
	
	public NBTTagCompound() {
		
	}
	
	public void put(String name, NBTBase base) {
		map.put(name, base);
	}
	
	public NBTBase get(String name) {
		return map.get(name);
	}
	
	/**
	 * @return the size of this compound
	 */
	public int size() {
		return map.size();
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	/**
	 * Remove all content from this compound
	 */
	public void clear() {
		map.clear();
	}
	
	public NBTBase remove(String name) {
		return map.remove(name);
	}
	
	public void writeRoot(ByteBuf writer) {
		for(String key : map.keySet()) {
			NBTBase base = map.get(key);
			if(base.getId() == TAG_END) {
				break;
			}
			
			byte[] name = key.getBytes(StandardCharsets.UTF_8);
			writer.writeByte(base.getId());
			writer.writeShort(name.length);
			writer.writeBytes(name);
			base.write(writer, 1);
		}
		
		writer.writeByte(TAG_END);
	}
	
	@Override
	protected int getId() {
		return TAG_COMPOUND;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		for(String key : map.keySet()) {
			NBTBase base = map.get(key);
			
			byte[] name = key.getBytes(StandardCharsets.UTF_8);
			writer.writeByte(base.getId());
			writer.writeShort(name.length);
			writer.writeBytes(name);
			base.write(writer, depth + 1);
		}
		
		writer.writeByte(TAG_END);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		while(reader.readableBytes() > 0) {
			int type = reader.readUnsignedByte();
			if(type == TAG_END) break;
			
			NBTBase base = NBTBase.createFromId(type);
			String name = "";
			int nameLength = reader.readShort();
			if(nameLength > 0) {
				name = new String(reader.readBytes(nameLength));
			}
			
			base.read(reader, depth + 1);
			put(name, base);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NBTTagCompound) {
			NBTTagCompound tag = (NBTTagCompound)obj;
			return tag.toString().equals(toString());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}
