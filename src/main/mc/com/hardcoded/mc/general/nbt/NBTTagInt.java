package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

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
	
	public void write(PacketIO writer, int depth) {
		writer.writeInt(value);
	}
	
	public void read(PacketIO reader, int depth) {
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
