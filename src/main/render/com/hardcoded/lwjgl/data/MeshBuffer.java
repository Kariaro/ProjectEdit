package com.hardcoded.lwjgl.data;

import com.hardcoded.lwjgl.mesh.Mesh;
import com.hardcoded.render.util.FloatArray;

public class MeshBuffer {
	public static final int DEFAULT_CAPACITY = 4096;
	
	private FloatArray vert;
	private FloatArray uv;
	private FloatArray col;
	
	public MeshBuffer() {
		vert = new FloatArray(DEFAULT_CAPACITY);
		uv   = new FloatArray(DEFAULT_CAPACITY);
		col  = new FloatArray(DEFAULT_CAPACITY);
	}
	
	public MeshBuffer pos(float... value) {
		vert.add(value);
		return this;
	}
	
	public MeshBuffer uv(float... value) {
		uv.add(value);
		return this;
	}
	
	public MeshBuffer col(float... value) {
		col.add(value);
		return this;
	}
	
	public void reset() {
		vert.reset(DEFAULT_CAPACITY);
		uv.reset(DEFAULT_CAPACITY);
		col.reset(DEFAULT_CAPACITY);
	}
	
	public Mesh build() {
		try {
			return new Mesh(vert, uv, col);
		} finally {
			reset();
		}
	}
}
