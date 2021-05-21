package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.PacketIO;

public class NBTTagString extends NBTBase {
	private String value;
	
	public NBTTagString(String name) {
		this(name, null);
	}
	
	public NBTTagString(String name, String value) {
		super(name, TAG_STRING);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void write(PacketIO writer, int depth) {
		if(value == null) {
			writer.writeShort(0);
		} else {
			writer.writeShort(value.length());
			writer.writeBytes(value.getBytes());
		}
	}
	
	public void read(PacketIO reader, int depth) {
		int valueLength = reader.readShort();
		if(valueLength == 0) {
			this.value = null;
		} else {
			this.value = new String(reader.readBytes(valueLength));
		}
	}
	
	@Override
	public Object getObjectValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
