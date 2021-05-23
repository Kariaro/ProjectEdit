package com.hardcoded.mc.general.nbt;

import java.util.Arrays;
import java.util.List;

import com.hardcoded.mc.general.ByteBuf;

public class NBTTagIntArray extends NBTBase {
	private int[] array;
	
	public NBTTagIntArray() {
		this.array = new int[0];
	}
	
	public NBTTagIntArray(int[] array) {
		this.array = array.clone();
	}
	
	public NBTTagIntArray(int[] array, int offset, int length) {
		setArray(array, offset, length);
	}
	
	public NBTTagIntArray(List<Integer> list) {
		this.array = list.stream().mapToInt(i -> i).toArray().clone();
	}
	
	public NBTTagIntArray(List<Integer> list, int offset, int length) {
		setArray(list.stream().mapToInt(i -> i).toArray(), offset, length);
	}
	
	public void setArray(List<Integer> list) {
		setArray(list.stream().mapToInt(i -> i).toArray());
	}
	
	public void setArray(List<Integer> list, int offset, int length) {
		setArray(list.stream().mapToInt(i -> i).toArray(), offset, length);
	}
	
	public void setArray(int[] array) {
		this.array = array.clone();
	}
	
	public void setArray(int[] array, int offset, int length) {
		this.array = new int[length];
		System.arraycopy(array, offset, array, 0, length);
	}
	
	public int[] getArray() {
		return array;
	}
	
	public void setValue(int index, int value) {
		this.array[index] = value;
	}
	
	@Override
	protected int getId() {
		return TAG_INT_ARRAY;
	}
	
	@Override
	public void write(ByteBuf writer, int depth) {
		final int len = array.length;
		writer.writeInt(len);
		for(int i = 0; i < len; i++) {
			writer.writeInt(array[i]);
		}
	}
	
	@Override
	public void read(ByteBuf reader, int depth) {
		int length = reader.readInt();
		array = new int[length];
		for(int i = 0; i < length; i++) {
			array[i] = reader.readInt();
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}
}
