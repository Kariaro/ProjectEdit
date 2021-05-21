package com.hardcoded.mc.general.nbt;

import java.util.Arrays;
import java.util.List;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagIntArray extends NBTBase {
	private int[] array;
	private int size;
	
	public NBTTagIntArray(String name, int[] array) {
		this(name, array, 0, array.length);
	}
	
	public NBTTagIntArray(String name, int[] array, int offset, int length) {
		super(name, TAG_INT_ARRAY);
		setArray(array, offset, length);
	}
	
	public NBTTagIntArray(String name, List<Integer> list) {
		this(name, list, 0, list.size());
	}
	
	public NBTTagIntArray(String name, List<Integer> list, int offset, int length) {
		super(name, TAG_INT_ARRAY);
		setArray(list, offset, length);
	}
	
	public void setArray(List<Integer> list) {
		setArray(list, 0, list.size());
	}
	
	public void setArray(List<Integer> list, int offset, int length) {
		this.size = length - offset;
		this.array = new int[size];
		Object[] values = list.toArray();
		for(int i = 0; i < size; i++) {
			this.array[i] = (int)values[i + offset];
		}
	}
	
	public void setArray(int[] array) {
		this.size = array.length;
		this.array = array.clone();
	}
	
	public void setArray(int[] array, int offset, int length) {
		this.size = length - offset;
		this.array = new int[size];
		for(int i = 0; i < size; i++) {
			this.array[i] = array[i + offset];
		}
	}
	
	public int[] getArray() {
		return array;
	}
	
	public void setValue(int index, int value) {
		this.array[index] = value;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		writer.writeInt(array.length);
		for(int i = 0; i < size; i++) {
			writer.writeInt(array[i]);
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readInt();
		int[] array = new int[length];
		for(int i = 0; i < length; i++) {
			array[i] = reader.readInt();
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
