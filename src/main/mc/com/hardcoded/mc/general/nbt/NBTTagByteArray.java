package com.hardcoded.mc.general.nbt;

import java.util.Arrays;
import java.util.List;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagByteArray extends NBTBase {
	private byte[] array;
	private int size;
	
	public NBTTagByteArray(String name, byte[] array) {
		this(name, array, 0, array.length);
	}
	
	public NBTTagByteArray(String name, byte[] array, int offset, int length) {
		super(name, TAG_BYTE_ARRAY);
		setArray(array, offset, length);
	}
	
	public NBTTagByteArray(String name, List<Byte> list) {
		this(name, list, 0, list.size());
	}
	
	public NBTTagByteArray(String name, List<Byte> list, int offset, int length) {
		super(name, TAG_BYTE_ARRAY);
		setArray(list, offset, length);
	}
	
	public void setArray(List<Byte> list) {
		setArray(list, 0, list.size());
	}
	
	public void setArray(List<Byte> list, int offset, int length) {
		this.size = length - offset;
		this.array = new byte[size];
		Object[] values = list.toArray();
		for(int i = 0; i < size; i++) {
			this.array[i] = (byte)values[i + offset];
		}
	}
	
	public void setArray(byte[] array) {
		this.size = array.length;
		this.array = array.clone();
	}
	
	public void setArray(byte[] array, int offset, int length) {
		this.size = length - offset;
		this.array = new byte[size];
		for(int i = 0; i < size; i++) {
			this.array[i] = array[i + offset];
		}
	}
	
	public byte[] getArray() {
		return array;
	}
	
	public void setValue(int index, int value) {
		this.array[index] = (byte)value;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeInt(array.length);
		for(int i = 0; i < size; i++) {
			writer.writeByte(array[i]);
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readInt();
		byte[] array = new byte[length];
		for(int i = 0; i < length; i++) {
			array[i] = reader.readByte();
		}
		setArray(array);
	}
	
	@Override
	public Object getObjectValue() {
		return array;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}
}
