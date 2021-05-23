package com.hardcoded.mc.general.nbt.copy;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.hardcoded.mc.general.ByteBuf;

/**
 * 
 * @author HardCoded
 */
public class NBTTagCompound extends NBTBase {
	private Map<String, NBTBase> map = new HashMap<String, NBTBase>();
	
	public NBTTagCompound() {
		super(null, TAG_COMPOUND);
	}
	
	public NBTTagCompound(String name) {
		super(name, TAG_COMPOUND);
	}
	
	public void put(NBTBase base) {
		map.put(base.getName(), base);
	}
	
	public void put(String name, NBTBase base) {
		base.setName(name);
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
		for(NBTBase base : map.values()) {
			if(base.getId() == TAG_END) {
				break;
			}

			String nameValue = base.getName();
			byte[] name = (nameValue == null ? "":nameValue).getBytes(StandardCharsets.UTF_8);
			writer.writeByte(base.getId());
			writer.writeShort(name.length);
			writer.writeBytes(name);
			
			base.write(writer, 1);
		}
		
		writer.writeByte(TAG_END);
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		if(depth == 0) {
			String nameValue = getName();
			byte[] name = (nameValue == null ? "":nameValue).getBytes(StandardCharsets.UTF_8);
			writer.writeByte(TAG_COMPOUND);
			writer.writeShort(name.length);
			writer.writeBytes(name);
		}
		
		for(NBTBase base : map.values()) {
			String nameValue = base.getName();
			byte[] name = (nameValue == null ? "":nameValue).getBytes(StandardCharsets.UTF_8);
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
			int nameLength = reader.readShort();
			if(nameLength > 0) {
				base.setName(new String(reader.readBytes(nameLength)));
			}
			
			base.read(reader, depth + 1);
			put(base);
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
	public Object getObjectValue() {
		return map;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}
