package com.hardcoded.utils;

import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;

public class Maths {
	public static Vector4f generateUv(FaceType face, Vector3f from, Vector3f to) {
		switch(face) {
			case south: // back
			case north: // front
				return new Vector4f(from.x, from.y, to.x, to.y);
			case east: // right
			case west: // left
				return new Vector4f(from.z, from.y, to.z, to.y);
			case up: // up
			case down: // down
				return new Vector4f(from.x, from.z, to.x, to.z);
			default:
				break;
		}
		
		throw new UnsupportedOperationException();
	}
	
	public static float[] getModelUv(FaceType face, TextureAtlas atlas, int atlas_id, Vector3f from, Vector3f to) {
		Vector4f uv = generateUv(face, from, to);
		float[] array = new float[] {
			uv.x, uv.w, uv.z, uv.w, uv.z, uv.y,
			uv.x, uv.w, uv.z, uv.y, uv.x, uv.y
		};
		
		atlas.transformModelUv(atlas_id, array);
		return array;
	}
	
	public static float[] getModelVertexes(FaceType face, Vector3f from, Vector3f to) {
		switch(face) {
			case north: // front
				return new float[] {
					to.x, from.y, from.z,
					from.x, from.y, from.z,
					from.x, to.y, from.z,
					
					to.x, from.y, from.z,
					from.x, to.y, from.z,
					to.x, to.y, from.z,
				};
			case south: // back
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
	
	public static void fastRotateX(float[] vertex, Vector3f origin, int index, float angle) {
		float sin = Math.sin(angle), cos = Math.cosFromSin(sin, angle);
		float y = vertex[index + 1] - origin.y;
		float z = vertex[index + 2] - origin.z;
		vertex[index + 1] = y * cos - z * sin + origin.y;
		vertex[index + 2] = y * sin + z * cos + origin.z;
	}
	
	public static void fastRotateY(float[] vertex, Vector3f origin, int index, float angle) {
		float sin = Math.sin(angle), cos = Math.cosFromSin(sin, angle);
		float x = vertex[index]     - origin.x;
		float z = vertex[index + 2] - origin.z;
		vertex[index]     =  x * cos + z * sin + origin.x;
		vertex[index + 2] = -x * sin + z * cos + origin.z;
	}
	
	public static void fastRotateZ(float[] vertex, Vector3f origin, int index, float angle) {
		float sin = Math.sin(angle), cos = Math.cosFromSin(sin, angle);
		float x = vertex[index]     - origin.x;
		float y = vertex[index + 1] - origin.y;
		vertex[index]     = x * cos - y * sin + origin.x;
		vertex[index + 1] = x * sin + y * cos + origin.y;
	}
}
