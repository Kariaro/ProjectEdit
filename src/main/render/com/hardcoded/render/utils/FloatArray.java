package com.hardcoded.render.utils;

import java.util.Arrays;

public final class FloatArray {
	private float[] array;
	private int index;
	
	public FloatArray(int capacity) {
		this.array = new float[capacity];
	}
	
	public void add(float value) {
		if(index == array.length) {
			// 1024 > 1512 > 2200 > 3100 > 5000
			float[] next = new float[array.length + (array.length >> 1)];
			System.arraycopy(array, 0, next, 0, array.length);
			next[index++] = value;
			array = next;
		} else {
			array[index++] = value;
		}
	}
	
	public void add(float... value) {
		if(index + value.length >= array.length) {
			// 1024 > 1512 > 2200 > 3100 > 5000
			float[] next = new float[array.length + (array.length >> 1) + value.length];
			System.arraycopy(array, 0, next, 0, array.length);
			array = next;
		}
		
		System.arraycopy(value, 0, array, index, value.length);
		index += value.length;
	}
	
	public float get(int index) {
		return array[index];
	}
	
	public void reset() {
		index = 0;
		array = new float[1024];
	}
	
	public int size() {
		return index;
	}
	
	public float[] toArray() {
		return Arrays.copyOf(array, index);
	}
	
	@Override
	public String toString() {
		if(index == 0) return "FloatArray {}";
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < index; i++) {
			sb.append(", ").append(String.format("%.4f", array[i]));
		}
		
		return "FloatArray { " + sb.toString().substring(2) + " }";
	}
}
