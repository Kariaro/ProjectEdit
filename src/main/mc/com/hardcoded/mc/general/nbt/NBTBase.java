package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

public abstract class NBTBase {
	public static final int TAG_END = 0;
	public static final int TAG_BYTE = 1;
	public static final int TAG_SHORT = 2;
	public static final int TAG_INT = 3;
	public static final int TAG_LONG = 4;
	public static final int TAG_FLOAT = 5;
	public static final int TAG_DOUBLE = 6;
	public static final int TAG_BYTE_ARRAY = 7;
	public static final int TAG_STRING = 8;
	public static final int TAG_LIST = 9;
	public static final int TAG_COMPOUND = 10;
	public static final int TAG_INT_ARRAY = 11;
	public static final int TAG_LONG_ARRAY = 12;
	
	private final int tagId;
	private String name;
	
	public NBTBase(String name, int tagId) {
		this.tagId = tagId;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return tagId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static NBTTagCompound readNBTTagCompound(PacketIO reader) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.read(reader, 0);
		
		if(tag.size() < 1) {
			return null;
		} else {
			return (NBTTagCompound)tag.get(null);
		}
	}
	
	public static void writeNBTTagCompound(PacketIO writer, NBTBase base) {
		if(base == null) {
			writer.writeByte(TAG_END);
			return;
		}
		
		base.write(writer, 0);
	}
	
	public static NBTBase createFromId(String name, int id) {
		switch(id) {
			case TAG_BYTE: return new NBTTagByte(name);
			case TAG_BYTE_ARRAY: return new NBTTagByteArray(name, new byte[0]);
			case TAG_COMPOUND: return new NBTTagCompound(name);
			case TAG_DOUBLE: return new NBTTagDouble(name);
			case TAG_FLOAT: return new NBTTagFloat(name);
			case TAG_INT: return new NBTTagInt(name);
			case TAG_INT_ARRAY: return new NBTTagIntArray(name, new int[0]);
			case TAG_LIST: return new NBTTagList<NBTBase>(name);
			case TAG_LONG_ARRAY: return new NBTTagLongArray(name, new long[0]);
			case TAG_SHORT: return new NBTTagShort(name);
			case TAG_STRING: return new NBTTagString(name);
			case TAG_END:
			default:
				return null;
		}
	}
	
	public abstract Object getObjectValue();
	public abstract void write(PacketIO writer, int depth);
	public abstract void read(PacketIO reader, int depth);
}
