package com.hardcoded.utils;

import java.util.*;

import org.joml.Vector3i;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.ChunkRender;
import com.hardcoded.lwjgl.data.Texture;

public class ModelJsonLoader {
//	static void deep_replace(Object object, Map<String, String> replace) {
//		if(object instanceof JSONObject) {
//			JSONObject json = (JSONObject)object;
//			
//			List<String> keys = new ArrayList<>(json.keySet());
//			for(String key : keys) {
//				Object value = json.get(key);
//				
//				if(value instanceof JSONObject || value instanceof JSONArray) {
//					deep_replace(value, replace);
//				} else if(value instanceof String) {
//					String str = (String)value;
//					
//					if(str.startsWith("#")) {
//						json.put(key, replace.get(str.substring(1)));
//					}
//				}
//			}
//		} else if(object instanceof JSONArray) {
//			JSONArray json = (JSONArray)object;
//			
//			for(int i = 0; i < json.length(); i++) {
//				Object value = json.get(i);
//				
//				if(value instanceof JSONObject || value instanceof JSONArray) {
//					deep_replace(value, replace);
//				} else if(value instanceof String) {
//					String str = (String)value;
//					
//					if(str.startsWith("#")) {
//						json.put(i, replace.get(str.substring(1)));
//					}
//				}
//			}
//		}
//	}
//
//	static void copy(String key, JSONObject source, JSONObject target) {
//		if(source.has(key)) target.put(key, source.get(key));
//	}
//	
//	public static enum Face {
//		down, up, north, south, west, east
//	}
//	
//	public static boolean hasFace(Face face, int faces) {
//		if(face == null) return true;
//		if(face == Face.up && (faces & ChunkRender.FACE_UP) != 0) return true;
//		if(face == Face.down && (faces & ChunkRender.FACE_DOWN) != 0) return true;
//		if(face == Face.north && (faces & ChunkRender.FACE_FRONT) != 0) return true;
//		if(face == Face.south && (faces & ChunkRender.FACE_BACK) != 0) return true;
//		if(face == Face.east && (faces & ChunkRender.FACE_RIGHT) != 0) return true;
//		if(face == Face.west && (faces & ChunkRender.FACE_LEFT) != 0) return true;
//		return false;
//	}
	
	public static final Random random = new Random();
//	public static class ModelFace {
//		public final Vector3i from;
//		public final Vector3i to;
//		public final Face face;
//		public final Face cullface;
//		public final Texture texture;
//		public float[] uv = {
//			0, 1, 1, 1, 1, 0,
//			0, 1, 1, 0, 0, 0
//		};
//		public float x_rot;
//		public float y_rot;
//		
//		public ModelFace(ModelObject parent, Face face, Face cullface, float[] uv, Texture texture) {
//			this.face = face;
//			this.from = parent.from;
//			if(uv != null) {
//				this.uv = uv;
//			}
//			this.to = parent.to;
//			this.cullface = cullface;
//			this.texture = texture;
//			
//			if(texture != null && uv != null) {
//				float width = texture.width;
//				float height = texture.height;
//				if(width < 1) width = 1;
//				if(height < 1) height = 1;
//				
//				for(int i = 0; i < 12; i += 2) {
//					this.uv[i] /= (float)width;
//					this.uv[i + 1] /= (float)height;
//				}
//			}
//		}
//		
//		public float[] uv() {
//			return uv;
//		}
//		
//		public float[] vertex() {
//			switch(face) {
//				case south: // back
//					return new float[] {
//						to.x, from.y, from.z,
//						from.x, from.y, from.z,
//						from.x, to.y, from.z,
//						
//						to.x, from.y, from.z,
//						from.x, to.y, from.z,
//						to.x, to.y, from.z,
//					};
//				case north: // front
//					return new float[] {
//						from.x, from.y, to.z,
//						to.x, from.y, to.z,
//						to.x, to.y, to.z,
//						
//						from.x, from.y, to.z,
//						to.x, to.y, to.z,
//						from.x, to.y, to.z
//					};
//				case east: // right
//					return new float[] {
//						to.x, from.y, to.z,
//						to.x, from.y, from.z,
//						to.x, to.y, from.z,
//						
//						to.x, from.y, to.z,
//						to.x, to.y, from.z,
//						to.x, to.y, to.z
//					};
//				case west: // left
//					return new float[] {
//						from.x, from.y, from.z,
//						from.x, from.y, to.z,
//						from.x, to.y, to.z,
//						
//						from.x, from.y, from.z,
//						from.x, to.y, to.z,
//						from.x, to.y, from.z
//					};
//				case up: // up
//					return new float[] {
//						from.x, to.y, to.z,
//						to.x, to.y, to.z,
//						to.x, to.y, from.z,
//						
//						from.x, to.y, to.z,
//						to.x, to.y, from.z,
//						from.x, to.y, from.z,
//					};
//				case down: // down
//					return new float[] {
//						from.x, from.y, from.z,
//						to.x, from.y, from.z,
//						to.x, from.y, to.z,
//						
//						from.x, from.y, from.z,
//						to.x, from.y, to.z,
//						from.x, from.y, to.z,
//					};
//			}
//			
//			throw new UnsupportedOperationException();
//		}
//	}
//	
//	public static class ModelObject {
//		public Vector3i from = new Vector3i();
//		public Vector3i to = new Vector3i();
//		public List<ModelFace> faces;
//		
//		public ModelObject() {
//			faces = new ArrayList<>();
//		}
//	}
	
//	private static final float dif() {
//		return (random.nextFloat() - 0.5f) / 10.0f;
//	}
	
//	public static class ModelBlock {
//		public List<ModelObject> list = new ArrayList<>();
//		private float[] rgba = new float[4];
//		public void render(float r, float g, float b, float a, int faces) {
//			//GL11.glColor4f(1, 1, 1, 1);
//			
//			GL11.glColor4f(r, g, b, a);
//			rgba[0] = r;
//			rgba[1] = g;
//			rgba[2] = b;
//			rgba[3] = a;
//			for(ModelObject model : list) {
//				renderModel(model, faces);
//			}
//		}
//		
//		private void renderModel(ModelObject model, int faces) {
//			for(ModelFace face : model.faces) {
//				if(!hasFace(face.cullface, faces)) continue;
//				renderFace(face);
//			}
//		}
//		
//		private void renderFace(ModelFace face) {
//			float[] vertex = face.vertex();
//			float[] uv = face.uv();
//			
//			if(face.texture != null) {
//				face.texture.bind();
//			}
//			
//			GL11.glBegin(GL11.GL_TRIANGLES);
//			for(int i = 0, len = vertex.length / 3; i < len; i++) {
//				int v = i * 3;
//				int t = i * 2;
//				
//				//GL11.glColor4f(rgba[0] + dif(), rgba[1] + dif(), rgba[2] + dif(), rgba[3] + dif());
//				GL11.glTexCoord2f(uv[t], uv[t + 1]);
//				GL11.glVertex3f(vertex[v], vertex[v + 1], vertex[v + 2]);
//			}
//			GL11.glEnd();
//			
//			if(face.texture != null) {
//				face.texture.unbind();
//			}
//		}
//	}
//	
//	@NotNull
//	public static ModelBlock createModel(VersionResourceReader resource, JSONObject json) {
//		if(!json.has("elements")) return new ModelBlock();
//		System.out.println("json: " + json);
//		
//		ModelBlock model_block = new ModelBlock();
//		List<ModelObject> objects = new ArrayList<>();
//		
//		JSONArray list = json.getJSONArray("elements");
//		for(int i = 0; i < list.length(); i++) {
//			JSONObject model = list.getJSONObject(i);
//			System.out.printf("    model[%d]: %s\n", i, model);
//			
//			ModelObject result = new ModelObject();
//			JSONArray from = model.getJSONArray("from");
//			JSONArray to = model.getJSONArray("to");
//			
//			result.from.x = from.getInt(0);
//			result.from.y = from.getInt(1);
//			result.from.z = from.getInt(2);
//			result.to.x = to.getInt(0);
//			result.to.y = to.getInt(1);
//			result.to.z = to.getInt(2);
//			
//			JSONObject faces = model.getJSONObject("faces");
//			for(String key : faces.keySet()) {
//				Face face = Face.valueOf(key);
//				JSONObject face_json = faces.getJSONObject(key);
//				
//				Face cullface = null;
//				Texture texture = null;
//				
//				if(face_json.has("cullface")) cullface = Face.valueOf(face_json.getString("cullface"));
//				if(face_json.has("texture")) {
//					texture = resource.readTexture(face_json.getString("texture"));
//				}
//				
//				float[] uv = null;
//				if(face_json.has("uv")) {
//					JSONArray face_uv = face_json.getJSONArray("uv");
//					// x1, y1, x2, y2
//					float x1 = face_uv.getInt(0);
//					float y1 = face_uv.getInt(1);
//					float x2 = face_uv.getInt(2);
//					float y2 = face_uv.getInt(3);
//					uv = new float[] {
//						x1, y2, x2, y2, x2, y1,
//						x1, y2, x2, y1, x1, y1
//					};
//				}
//				
//				ModelFace model_face = new ModelFace(result, face, cullface, uv, texture);
//				
//				result.faces.add(model_face);
//			}
//			
//			objects.add(result);
//		}
//		
//		model_block.list.addAll(objects);
//		return model_block;
//	}
}
