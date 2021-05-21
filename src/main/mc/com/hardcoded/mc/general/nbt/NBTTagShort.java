package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

public class NBTTagShort extends NBTBase {
	private short value;
	
	public NBTTagShort(String name) {
		this(name, 0);
	}
	
	public NBTTagShort(String name, int value) {
		super(name, TAG_SHORT);
		this.value = (short)value;
	}
	
	public short getValue() {
		return (short)value;
	}
	
	public void setValue(int value) {
		this.value = (short)value;
	}
	
	public void write(PacketIO writer, int depth) {
		writer.writeShort(value);
	}
	
	public void read(PacketIO reader, int depth) {
		this.value = reader.readShort();
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Short.toString(value);
	}
}
