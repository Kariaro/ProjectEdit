package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

public class NBTTagFloat extends NBTBase {
	private float value;
	
	public NBTTagFloat(String name) {
		this(name, 0);
	}
	
	public NBTTagFloat(String name, float value) {
		super(name, TAG_FLOAT);
		this.value = value;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public void write(PacketIO writer, int depth) {
		writer.writeFloat(value);
	}
	
	public void read(PacketIO reader, int depth) {
		this.value = reader.readFloat();
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return Float.toString(value);
	}
}
