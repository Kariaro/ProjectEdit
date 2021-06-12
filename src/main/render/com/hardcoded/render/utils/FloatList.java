package com.hardcoded.render.utils;

import java.util.*;

@Deprecated
public class FloatList {
	private float[] array;
	private int size;
	
	public FloatList(int initialCapacity) {
		this.array = new float[initialCapacity];
	}

	private float[] grow(int minCapacity) {
		int oldCapacity = array.length;
		return array = Arrays.copyOf(array, Math.max(minCapacity - oldCapacity, oldCapacity >> 1) + oldCapacity);
	}

	private float[] grow() {
		int oldCapacity = array.length;
		return array = Arrays.copyOf(array, Math.max(size + 1 - oldCapacity, oldCapacity >> 1) + oldCapacity);
	}
	
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public float[] toArray() {
		return Arrays.copyOf(array, size);
	}
	
	public void clear() {
		size = 0;
	}
	
	public float get(int index) {
		return array[index];
	}
	
	public float set(int index, float value) {
		float oldValue = array[index];
		array[index] = value;
		return oldValue;
	}
	
	public boolean add(float value) {
		if(size == array.length) array = grow();
		array[size++] = value;
		return true;
	}
	
	public boolean add(float... a) {
		int numNew = a.length;
		final int s;
		if(numNew > array.length - (s = size)) grow(s + numNew);
		System.arraycopy(a, 0, array, s, numNew);
		size = s + numNew;
		return true;
	}
	
//	public float remove(int index) {
//		final float[] es = array;
//		
//		float oldValue = es[index];
//		final int newSize;
//		if((newSize = size - 1) > index)
//			System.arraycopy(es, index + 1, es, index, newSize - index);
//		size = newSize;
//	
//		return oldValue;
//	}
	
	@Override
	public int hashCode() {
		final float[] es = array;
		
		int hashCode = 1;
		for(int i = 0, len = size; i < len; i++) {
			hashCode = 31 * hashCode + Float.floatToIntBits(es[i]);
		}
		
		return hashCode;
	}
	
	@Override
	public String toString() {
		if(size == 0) return "FloatList {}";
		final float[] es = array;
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0, len = size; i < len; i++) {
			sb.append(", ").append(String.format("%.4f", es[i]));
		}
		
		return "FloatList { " + sb.toString().substring(2) + " }";
	}
}
