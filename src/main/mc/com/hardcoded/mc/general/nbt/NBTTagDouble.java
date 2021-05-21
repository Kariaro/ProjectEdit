package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

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
	
	public void write(PacketIO writer, int depth) {
		writer.writeDouble(value);
	}
	
	public void read(PacketIO reader, int depth) {
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
