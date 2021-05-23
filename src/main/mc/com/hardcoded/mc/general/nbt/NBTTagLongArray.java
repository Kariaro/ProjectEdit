package com.hardcoded.mc.general.nbt;

import java.util.Arrays;
import java.util.List;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagLongArray extends NBTBase {
	private long[] array;
	
	public NBTTagLongArray() {
		array = new long[0];
	}
	
	public NBTTagLongArray(long[] array) {
		this.array = array.clone();
	}
	
	public NBTTagLongArray(long[] array, int offset, int length) {
		setArray(array, offset, length);
	}
	
	public NBTTagLongArray(List<Long> list) {
		this.array = list.stream().mapToLong(i -> i).toArray().clone();
	}
	
	public NBTTagLongArray(List<Long> list, int offset, int length) {
		setArray(list.stream().mapToLong(i -> i).toArray(), offset, length);
	}
	
	
	public void setArray(List<Long> list) {
		this.array = list.stream().mapToLong(i -> i).toArray().clone();
	}
	
	public void setArray(List<Long> list, int offset, final int length) {
		setArray(list.stream().mapToLong(i -> i).toArray(), offset, length);
	}
	
	public void setArray(long[] array) {
		this.array = array.clone();
	}
	
	public void setArray(long[] array, int offset, int length) {
		this.array = new long[length];
		System.arraycopy(array, offset, this.array, 0, length);
	}
	
	public long[] getArray() {
		return array;
	}
	
	public void setValue(int index, long value) {
		this.array[index] = value;
	}
	
	public int size() {
		return array.length;
	}
	
	@Override
	protected int getId() {
		return TAG_LONG_ARRAY;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		final int len = array.length;
		writer.writeInt(len);
		for(int i = 0; i < len; i++) {
			writer.writeLong(array[i]);
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readInt();
		array = new long[length];
		for(int i = 0; i < length; i++) {
			array[i] = reader.readLong();
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}
}
