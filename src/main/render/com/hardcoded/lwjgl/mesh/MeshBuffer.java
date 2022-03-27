package com.hardcoded.lwjgl.mesh;

import com.hardcoded.render.util.FloatArray;

public class MeshBuffer {
	// 6 sides *
	// 2 triangles *
	// 3 vertices *
	// 3 floats (x, y, z) = 108
	// 
	// Times 32 cubes = 3456
	public static final int DEFAULT_CAPACITY = 8192;
	public static final int _VERTS	= 0x1,
							_UV		= 0x2,
							_COLOR	= 0x4,
							_NORMAL	= 0x8;
	
	private final int flags;
	public FloatArray verts;
	public FloatArray uvs;
	public FloatArray colors;
	private FloatArray normals;
	
	public MeshBuffer() {
		this(_VERTS | _UV | _COLOR);
	}
	
	public MeshBuffer(int flags) {
		this.flags = flags;
		verts	= new FloatArray(DEFAULT_CAPACITY);
		uvs		= ((flags & _UV) == 0) ? null:new FloatArray(DEFAULT_CAPACITY);
		colors	= ((flags & _COLOR) == 0) ? null:new FloatArray(DEFAULT_CAPACITY);
		normals	= ((flags & _NORMAL) == 0) ? null:new FloatArray(DEFAULT_CAPACITY);
	}
	
	public int getFlags() {
		return flags;
	}
	
	public MeshBuffer pos(float x, float y, float z) {
		verts.add(x, y, z);
		return this;
	}
	
	public MeshBuffer pos(float[] array) {
		verts.add(array);
		return this;
	}
	
	public MeshBuffer uv(float x, float y) {
		uvs.add(x, y);
		return this;
	}
	
	public MeshBuffer uv(float[] array) {
		uvs.add(array);
		return this;
	}
	
	public MeshBuffer color(float r, float g, float b) {
		colors.add(r, g, b);
		return this;
	}
	
	public MeshBuffer color(float[] array) {
		colors.add(array);
		return this;
	}
	
	public MeshBuffer normal(float x, float y, float z) {
		normals.add(x, y, z);
		return this;
	}
	
	public Mesh build() {
		try {
			if((flags & _COLOR) != 0) {
				return new Mesh(verts.toArray(), uvs.toArray(), colors.toArray());
			}
			
			Mesh mesh = new Mesh(verts.toArray(), uvs.toArray());
			return mesh;
		} finally {
			if(verts != null) {
				verts.reset();
				verts = null;
			}
			
			if(uvs != null) {
				uvs.reset();
				uvs = null;
			}
			
			if(colors != null) {
				colors.reset();
				colors = null;
			}
		}
	}
}
