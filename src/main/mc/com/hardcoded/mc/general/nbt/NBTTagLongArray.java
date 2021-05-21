package com.hardcoded.mc.general.nbt;

import java.util.Arrays;
import java.util.List;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagLongArray extends NBTBase {
	private long[] array;
	private int size;
	
	public NBTTagLongArray(String name, long[] array) {
		this(name, array, 0, array.length);
	}
	
	public NBTTagLongArray(String name, long[] array, int offset, int length) {
		super(name, TAG_LONG_ARRAY);
		setArray(array, offset, length);
	}
	
	public NBTTagLongArray(String name, List<Long> list) {
		this(name, list, 0, list.size());
	}
	
	public NBTTagLongArray(String name, List<Long> list, int offset, int length) {
		super(name, TAG_LONG_ARRAY);
		setArray(list, offset, length);
	}
	
	public void setArray(List<Long> list) {
		setArray(list, 0, list.size());
	}
	
	public void setArray(List<Long> list, int offset, int length) {
		this.size = length - offset;
		this.array = new long[size];
		Object[] values = list.toArray();
		for(int i = 0; i < size; i++) {
			this.array[i] = (long)values[i + offset];
		}
	}
	
	public void setArray(long[] array) {
		this.size = array.length;
		this.array = array.clone();
	}
	
	public void setArray(long[] array, int offset, int length) {
		this.size = length - offset;
		this.array = new long[size];
		for(int i = 0; i < size; i++) {
			this.array[i] = array[i + offset];
		}
	}
	
	public long[] getArray() {
		return array;
	}
	
	public void setValue(int index, long value) {
		this.array[index] = value;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeInt(array.length);
		for(int i = 0; i < size; i++) {
			writer.writeLong(array[i]);
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readInt();
		long[] array = new long[length];
		for(int i = 0; i < length; i++) {
			array[i] = reader.readLong();
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
