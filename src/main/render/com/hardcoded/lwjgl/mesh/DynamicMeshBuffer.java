package com.hardcoded.lwjgl.mesh;

import com.hardcoded.render.util.FloatArray;

public class DynamicMeshBuffer {
	public static final int DEFAULT_CAPACITY = 4096;
	
	private DynamicMesh mesh;
	private FloatArray vert;
	private FloatArray uv;
	private FloatArray col;
	private boolean dirty;
	
	public DynamicMeshBuffer() {
		vert = new FloatArray(DEFAULT_CAPACITY);
		uv   = new FloatArray(DEFAULT_CAPACITY);
		col  = new FloatArray(DEFAULT_CAPACITY);
		mesh = new DynamicMesh();
	}
	
	public DynamicMeshBuffer pos(float... value) {
		dirty = true;
		vert.add(value);
		return this;
	}
	
	public DynamicMeshBuffer uv(float... value) {
		dirty = true;
		uv.add(value);
		return this;
	}
	
	public DynamicMeshBuffer col(float... value) {
		dirty = true;
		col.add(value);
		return this;
	}
	
	public void reset() {
		vert.reset(DEFAULT_CAPACITY);
		uv.reset(DEFAULT_CAPACITY);
		col.reset(DEFAULT_CAPACITY);
	}
	
	public void cleanup() {
		if(mesh == null) return;
		reset();
		mesh.cleanup();
		mesh = null;
		vert = null;
		uv = null;
		col = null;
	}
	
	protected DynamicMesh build() {
		mesh.upload(vert, uv, col);
		reset();
		return mesh;
	}
	
	public Mesh buildStaticMesh() {
		return new Mesh(vert.toArray(), uv.toArray(), col.toArray(), vert.size() / 3);
	}

	public void render() {
		if(dirty) {
			mesh.upload(vert, uv, col);
			reset();
		}
		
		mesh.render();
	}
}
