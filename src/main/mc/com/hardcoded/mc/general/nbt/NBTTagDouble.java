package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagDouble extends NBTBase {
	private double value;
	
	public NBTTagDouble(String name) {
		this(name, 0);
	}
	
	public NBTTagDouble(String name, double value) {
		super(name, TAG_DOUBLE);
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeDouble(value);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		this.value = reader.readDouble();
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Double.toString(value);
	}
}
