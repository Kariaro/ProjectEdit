package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.ByteBuf;

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
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeFloat(value);
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
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
