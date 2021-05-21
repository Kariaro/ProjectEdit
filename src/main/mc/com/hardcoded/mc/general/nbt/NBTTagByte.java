package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

public class NBTTagByte extends NBTBase {
	private byte value;
	
	public NBTTagByte(String name) {
		this(name, 0);
	}
	
	public NBTTagByte(String name, int value) {
		super(name, TAG_BYTE);
		this.value = (byte)value;
	}
	
	public byte getValue() {
		return (byte)value;
	}
	
	public void setValue(int value) {
		this.value = (byte)value;
	}

	public void write(PacketIO writer, int depth) {
		writer.writeByte(value);
	}
	
	public void read(PacketIO reader, int depth) {
		this.value = reader.readByte();
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Byte.toString(value);
	}
}
