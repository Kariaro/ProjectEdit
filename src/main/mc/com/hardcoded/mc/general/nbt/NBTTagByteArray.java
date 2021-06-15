package com.hardcoded.mc.general.nbt;

import java.util.Arrays;
import java.util.List;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagByteArray extends NBTBase {
	private byte[] array;
	
	public NBTTagByteArray() {
		
	}
	
	public NBTTagByteArray(byte[] array) {
		this(array, 0, array.length);
	}
	
	public NBTTagByteArray(byte[] array, int offset, int length) {
		setArray(array, offset, length);
	}
	
	public NBTTagByteArray(List<Byte> list) {
		this(list, 0, list.size());
	}
	
	public NBTTagByteArray(List<Byte> list, int offset, int length) {
		setArray(list, offset, length);
	}
	
	public void setArray(List<Byte> list) {
		setArray(list, 0, list.size());
	}
	
	public void setArray(List<Byte> list, int offset, final int length) {
		this.array = new byte[length];
		Byte[] values = list.toArray(Byte[]::new);
		for(int i = 0; i < length; i++) {
			this.array[i] = (byte)values[i + offset];
		}
	}
	
	public void setArray(byte[] array) {
		this.array = array.clone();
	}
	
	public void setArray(byte[] array, int offset, int length) {
		this.array = new byte[length];
		System.arraycopy(array, offset, this.array, 0, length);
	}
	
	public byte[] getArray() {
		return array;
	}
	
	public void setValue(int index, int value) {
		this.array[index] = (byte)value;
	}
	
	@Override
	public int getId() {
		return TAG_BYTE_ARRAY;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		final int len = array.length;
		writer.writeInt(len);
		for(int i = 0; i < len; i++) {
			writer.writeByte(array[i]);
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readInt();
		array = new byte[length];
		for(int i = 0; i < length; i++) {
			array[i] = reader.readByte();
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}
}
