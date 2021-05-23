package com.hardcoded.mc.general.nbt;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagString extends NBTBase {
	private String value;
	
	public NBTTagString() {
		this.value = "";
	}
	
	public NBTTagString(String value) {
		this.value = (value == null) ? "":value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = (value == null) ? "":value;
	}
	
	@Override
	protected int getId() {
		return TAG_STRING;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeShort(value.length());
		
		if(!value.isEmpty()) {
			writer.writeBytes(value.getBytes());
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readShort();
		if(length == 0) {
			this.value = "";
		} else {
			this.value = new String(reader.readBytes(length));
		}
	}
	
	@Override
	public String toString() {
		return value;
	}
}
