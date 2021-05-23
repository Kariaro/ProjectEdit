package com.hardcoded.mc.general.nbt.copy;

import com.hardcoded.mc.general.ByteBuf;

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
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeShort(value);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
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
