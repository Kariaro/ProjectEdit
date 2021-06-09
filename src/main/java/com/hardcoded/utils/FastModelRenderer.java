package com.hardcoded.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelObject;

public class FastModelRenderer {
	public static boolean hasFace(FaceType face, int faces) {
		return (face.getFlags() & faces) != 0;
	}
	
	public static void renderModelFast(BlockData bs, float x, float y, float z, int faces) {
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int i = 0; i < bs.model_objects.size(); i++) {
			Matrix4f mat = new Matrix4f()
				.translate(x, y, z)
				.scale(1 / 16.0f)
				.mul(bs.model_transform.get(i));
			
			for(ModelElement element : bs.model_objects.get(i).elements) {
				renderElementFast(element, mat, faces);
			}
		}
		GL11.glEnd();
	}
	
	private static void renderElementFast(ModelElement element, Matrix4f mat, int faces) {
		for(FaceType type : element.faces.keySet()) {
			if((type.getFlags() & faces) == 0) continue;
			renderFaceFast(element.faces.get(type), mat);
		}
	}
	
	private static void renderFaceFast(ModelFace face, Matrix4f mat) {
		float[] vertex = face.vertex;
		float[] uv = face.uv;
		
		for(int i = 0, len = vertex.length / 3; i < len; i++) {
			int v = i * 3;
			int t = i * 2;
			
			//GL11.glVertex3f(vertex[v], vertex[v + 1], vertex[v + 2]);
			Vector3f p = mat.transformPosition(new Vector3f(vertex[v], vertex[v + 1], vertex[v + 2]));
			GL11.glTexCoord2f(uv[t], uv[t + 1]);
			GL11.glVertex3f(p.x, p.y, p.z);
		}
	}
	
	public static void renderModel(ModelObject object, int faces) {
		for(ModelElement element : object.elements) {
			renderElement(element, faces);
		}
	}
	
	private static void renderElement(ModelElement element, int faces) {
		for(FaceType type : element.faces.keySet()) {
			if((type.getFlags() & faces) == 0) continue;
			renderFace(element.faces.get(type));
		}
	}
	
	private static void renderFace(ModelFace face) {
		float[] vertex = face.vertex;
		float[] uv = face.uv;
		
		GL11.glBegin(GL11.GL_TRIANGLES);
		for(int i = 0, len = vertex.length / 3; i < len; i++) {
			int v = i * 3;
			int t = i * 2;
			
			GL11.glTexCoord2f(uv[t], uv[t + 1]);
			GL11.glVertex3f(vertex[v], vertex[v + 1], vertex[v + 2]);
		}
		GL11.glEnd();
	}
}
