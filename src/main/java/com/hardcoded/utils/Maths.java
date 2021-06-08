package com.hardcoded.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;

public class Maths {
	public static float[] getModelUvs(Vector4f uv, Texture texture) {
		float[] array = new float[] {
			uv.x, uv.w, uv.z, uv.w, uv.z, uv.y,
			uv.x, uv.w, uv.z, uv.y, uv.x, uv.y
		};
		
		if(texture != null && uv != null) {
			float width = texture.width;
			float height = texture.height;
			if(width < 1) width = 1;
			if(height < 1) height = 1;
			
			for(int i = 0; i < 12; i += 2) {
				array[i] /= (float)width;
				array[i + 1] /= (float)height;
			}
		}
		
		return array;
	}
	
	public static float[] getModelVertexes(FaceType face, Vector3f from, Vector3f to) {
		switch(face) {
			case south: // back
				return new float[] {
					to.x, from.y, from.z,
					from.x, from.y, from.z,
					from.x, to.y, from.z,
					
					to.x, from.y, from.z,
					from.x, to.y, from.z,
					to.x, to.y, from.z,
				};
			case north: // front
				return new float[] {
					from.x, from.y, to.z,
					to.x, from.y, to.z,
					to.x, to.y, to.z,
					
					from.x, from.y, to.z,
					to.x, to.y, to.z,
					from.x, to.y, to.z
				};
			case east: // right
				return new float[] {
					to.x, from.y, to.z,
					to.x, from.y, from.z,
					to.x, to.y, from.z,
					
					to.x, from.y, to.z,
					to.x, to.y, from.z,
					to.x, to.y, to.z
				};
			case west: // left
				return new float[] {
					from.x, from.y, from.z,
					from.x, from.y, to.z,
					from.x, to.y, to.z,
					
					from.x, from.y, from.z,
					from.x, to.y, to.z,
					from.x, to.y, from.z
				};
			case up: // up
				return new float[] {
					from.x, to.y, to.z,
					to.x, to.y, to.z,
					to.x, to.y, from.z,
					
					from.x, to.y, to.z,
					to.x, to.y, from.z,
					from.x, to.y, from.z,
				};
			case down: // down
				return new float[] {
					from.x, from.y, from.z,
					to.x, from.y, from.z,
					to.x, from.y, to.z,
					
					from.x, from.y, from.z,
					to.x, from.y, to.z,
					from.x, from.y, to.z,
				};
			default:
				break;
		}
		
		throw new UnsupportedOperationException();
	}
}
