package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagLong extends NBTBase {
	private long value;
	
	public NBTTagLong(String name) {
		this(name, 0);
	}
	
	public NBTTagLong(String name, long value) {
		super(name, TAG_LONG);
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	public void setValue(long value) {
		this.value = value;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeLong(value);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		this.value = reader.readLong();
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Long.toString(value);
	}
}
