package com.hardcoded.lwjgl.mesh;

public class MeshBuilder {
	public static final int _OPAQUE = 0;
	public static final int _TRANSLUCENT = 1;
	
	private static final int MESH_FIELDS = MeshBuffer._VERTS  | MeshBuffer._UV | MeshBuffer._COLOR;
	public MeshBuffer opaque;
	public MeshBuffer translucent;
	
	public Mesh opaqueMesh;
	public Mesh translucentMesh;
	
	public void reset() {
		opaque = new MeshBuffer(MESH_FIELDS);
		translucent = new MeshBuffer(MESH_FIELDS);
	}
	
	public MeshBuffer getBuffer(int id) {
		switch(id) {
			case _OPAQUE: {
				return opaque;
			}
			case _TRANSLUCENT: {
				return translucent;
			}
		}
		
		return null;
	}
	
	public Mesh getMesh(int id) {
		switch(id) {
			case _OPAQUE: {
				return opaqueMesh;
			}
			case _TRANSLUCENT: {
				return translucentMesh;
			}
		}
		
		return null;
	}
	
	public void build() {
		cleanup();

		if(opaque != null && opaque.verts.size() > 0) {
			opaqueMesh = opaque.build();
		}
		
		if(translucent != null && translucent.verts.size() > 0) {
			translucentMesh = translucent.build();
		}
		
		opaque = null;
		translucent = null;
	}
	
	public void cleanup() {
		if(opaqueMesh != null) {
			opaqueMesh.cleanup();
			opaqueMesh = null;
		}
		
		if(translucentMesh != null) {
			translucentMesh.cleanup();
			translucentMesh = null;
		}
	}
}
