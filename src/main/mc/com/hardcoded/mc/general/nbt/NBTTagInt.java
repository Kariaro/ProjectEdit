package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagInt extends NBTBase {
	private int value;
	
	public NBTTagInt(String name) {
		this(name, 0);
	}
	
	public NBTTagInt(String name, int value) {
		super(name, TAG_INT);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeInt(value);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		this.value = reader.readInt();
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}
}
