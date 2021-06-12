package com.hardcoded.utils;

import org.joml.Math;
import org.joml.Matrix4f;

import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.render.utils.FloatArray;
import com.hardcoded.render.utils.MeshBuilder;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;

public class FastModelRenderer {
	public static boolean hasFace(FaceType face, int faces) {
		return (face.getFlags() & faces) != 0;
	}
	
	public static interface Test {
		static void renderModelFast(BlockData bs, float x, float y, float z, FloatArray vertexBuffer, FloatArray uvBuffer, int faces) {
			Matrix4f pre = new Matrix4f()
				.translate(x, y, z)
				.scale(1 / 16.0f);
			
			for(int i = 0; i < bs.model_objects.size(); i++) {
				Matrix4f mat = new Matrix4f(pre)
					.mul(bs.model_transform.get(i));
				
				float m00 = mat.m00(), m01 = mat.m01(), m02 = mat.m02();
				float m10 = mat.m10(), m11 = mat.m11(), m12 = mat.m12();
				float m20 = mat.m20(), m21 = mat.m21(), m22 = mat.m22();
				float m30 = mat.m30(), m31 = mat.m31(), m32 = mat.m32();
				
				for(ModelElement element : bs.model_objects.get(i).elements) {
					for(FaceType type : element.faces.keySet()) {
//						FaceType test = FaceType.getFromNormal(mat.transformDirection(type.normal()));
						FaceType test = type.rotate(mat);
						
						if(type != FaceType.none && (test.getFlags() & faces) == 0) continue;
						
						ModelFace face = element.faces.get(type);
						uvBuffer.add(face.uv);
						
						float[] vertex = face.vertex;
						for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
							float ax = vertex[vi];
							float ay = vertex[vi + 1];
							float az = vertex[vi + 2];
					        float px = Math.fma(m00, ax, Math.fma(m10, ay, Math.fma(m20, az, m30)));
					        float py = Math.fma(m01, ax, Math.fma(m11, ay, Math.fma(m21, az, m31)));
					        float pz = Math.fma(m02, ax, Math.fma(m12, ay, Math.fma(m22, az, m32)));
							vertexBuffer.add(px, py, pz);
						}
						
//						renderFaceFast(element.faces.get(type), vertexBuffer, mat);
					}
				}
			}
		}
	}
	
	public static interface Fast {
		static void renderModelFast(BlockData bs, float x, float y, float z, MeshBuilder builder, int faces) {
			Matrix4f pre = new Matrix4f()
				.translate(x, y, z)
				.scale(1 / 16.0f);
			
			for(int i = 0; i < bs.model_objects.size(); i++) {
				Matrix4f mat = new Matrix4f(pre)
					.mul(bs.model_transform.get(i));
				
				float m00 = mat.m00(), m01 = mat.m01(), m02 = mat.m02();
				float m10 = mat.m10(), m11 = mat.m11(), m12 = mat.m12();
				float m20 = mat.m20(), m21 = mat.m21(), m22 = mat.m22();
				float m30 = mat.m30(), m31 = mat.m31(), m32 = mat.m32();
				
				for(ModelElement element : bs.model_objects.get(i).elements) {
					for(FaceType type : element.faces.keySet()) {
						FaceType test = type.rotate(mat);
						if(type == FaceType.none || (test.getFlags() & faces) != 0) {
							ModelFace face = element.faces.get(type);
							builder.uv(face.uv);
							
							float[] vertex = face.vertex;
							for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
								float ax = vertex[vi];
								float ay = vertex[vi + 1];
								float az = vertex[vi + 2];
						        float px = Math.fma(m00, ax, Math.fma(m10, ay, Math.fma(m20, az, m30)));
						        float py = Math.fma(m01, ax, Math.fma(m11, ay, Math.fma(m21, az, m31)));
						        float pz = Math.fma(m02, ax, Math.fma(m12, ay, Math.fma(m22, az, m32)));
						        builder.pos(px, py, pz);
							}
						}
					}
				}
			}
		}
	}
	

	
//	static void renderFaceFast(ModelFace face, FloatList vertexBuffer, Matrix4f mat) {
//		float[] vertex = face.vertex;
//		
//		for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
////			Vector3f p = mat.transformPosition(new Vector3f(vertex[vi], vertex[vi + 1], vertex[vi + 2]));
//			
//			float x = vertex[vi];
//			float y = vertex[vi + 1];
//			float z = vertex[vi + 2];
//	        float px = Math.fma(mat.m00(), x, Math.fma(mat.m10(), y, Math.fma(mat.m20(), z, mat.m30())));
//	        float py = Math.fma(mat.m01(), x, Math.fma(mat.m11(), y, Math.fma(mat.m21(), z, mat.m31())));
//	        float pz = Math.fma(mat.m02(), x, Math.fma(mat.m12(), y, Math.fma(mat.m22(), z, mat.m32())));
//			
//			// v.mulPosition(this)
//			
////			vertexBuffer.add(p.x, p.y, p.z);
//			vertexBuffer.add(px, py, pz);
//		}
//	}
}
