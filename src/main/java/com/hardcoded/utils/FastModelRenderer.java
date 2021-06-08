package com.hardcoded.utils;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.ChunkRender;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelObject;

public class FastModelRenderer {
	public static boolean hasFace(FaceType face, int faces) {
		if(face == null) return true;
		if(face == FaceType.none) return true;
		if(face == FaceType.up && (faces & ChunkRender.FACE_UP) != 0) return true;
		if(face == FaceType.down && (faces & ChunkRender.FACE_DOWN) != 0) return true;
		if(face == FaceType.north && (faces & ChunkRender.FACE_FRONT) != 0) return true;
		if(face == FaceType.south && (faces & ChunkRender.FACE_BACK) != 0) return true;
		if(face == FaceType.east && (faces & ChunkRender.FACE_RIGHT) != 0) return true;
		if(face == FaceType.west && (faces & ChunkRender.FACE_LEFT) != 0) return true;
		return false;
	}
	
	public static void renderModel(ModelObject object, Vector4f col, int faces) {
		GL11.glColor4f(col.x, col.y, col.z, col.w);
		for(ModelElement element : object.elements) {
			renderElement(element, faces);
		}
	}
	
	private static void renderElement(ModelElement element, int faces) {
		for(FaceType type : element.faces.keySet()) {
			if(!hasFace(type, faces)) continue;
			renderFace(element.faces.get(type));
		}
	}
	
	private static void renderFace(ModelFace face) {
		float[] vertex = face.vertex;
		float[] uv = face.uv;
		
		if(face.texture != null) {
			face.texture.bind();
		}
		
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int i = 0, len = vertex.length / 3; i < len; i++) {
			int v = i * 3;
			int t = i * 2;
			
			//GL11.glColor4f(rgba[0] + dif(), rgba[1] + dif(), rgba[2] + dif(), rgba[3] + dif());
			GL11.glTexCoord2f(uv[t], uv[t + 1]);
			GL11.glVertex3f(vertex[v], vertex[v + 1], vertex[v + 2]);
		}
		GL11.glEnd();
		
		if(face.texture != null) {
			face.texture.unbind();
		}
	}
}
