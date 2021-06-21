package com.hardcoded.render.utils;

import com.hardcoded.lwjgl.mesh.Mesh;

public class MeshBuilder {
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
	
	public MeshBuilder() {
		this(_VERTS | _UV | _COLOR);
	}
	
	public MeshBuilder(int flags) {
		this.flags = flags;
		verts	= new FloatArray(DEFAULT_CAPACITY);
		uvs		= ((flags & _UV) == 0) ? null:new FloatArray(DEFAULT_CAPACITY);
		colors	= ((flags & _COLOR) == 0) ? null:new FloatArray(DEFAULT_CAPACITY);
		normals	= ((flags & _NORMAL) == 0) ? null:new FloatArray(DEFAULT_CAPACITY);
	}
	
	public int getFlags() {
		return flags;
	}
	
	public MeshBuilder pos(float x, float y, float z) {
		verts.add(x, y, z);
		return this;
	}
	
	public MeshBuilder pos(float[] array) {
		verts.add(array);
		return this;
	}
	
	public MeshBuilder uv(float x, float y) {
		uvs.add(x, y);
		return this;
	}
	
	public MeshBuilder uv(float[] array) {
		uvs.add(array);
		return this;
	}
	
	public MeshBuilder color(float r, float g, float b, float a) {
		colors.add(r, g, b, a);
		return this;
	}
	
	public MeshBuilder color(float[] array) {
		colors.add(array);
		return this;
	}
	
	public MeshBuilder normal(float x, float y, float z) {
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
				verts = null;
			}
			
			if(uvs != null) {
				uvs = null;
			}
			
			if(colors != null) {
				colors = null;
			}
		}
	}
}
