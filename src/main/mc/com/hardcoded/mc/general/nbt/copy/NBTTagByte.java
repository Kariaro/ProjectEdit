package com.hardcoded.mc.general.nbt.copy;

import com.hardcoded.mc.general.ByteBuf;

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
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeByte(value);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
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
