package com.hardcoded.utils;

import java.lang.Math;
import java.text.NumberFormat;
import java.util.*;

import org.joml.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.render.LwjglRender;

public class FastModelJsonLoader {
	private static final JsonFactory factory = new JsonFactory();
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static enum FaceType {
		down(Blocks.FACE_DOWN),
		bottom(Blocks.FACE_DOWN),
		
		up(Blocks.FACE_UP),
		north(Blocks.FACE_FRONT),
		south(Blocks.FACE_BACK),
		west(Blocks.FACE_LEFT),
		east(Blocks.FACE_RIGHT),
		none(0);
		
		private int flags;
		private FaceType(int flags) {
			this.flags = flags;
		}
		
		public Vector3f normal() {
			switch(flags) {
				case Blocks.FACE_DOWN: return new Vector3f(0, -1, 0);
				case Blocks.FACE_UP: return new Vector3f(0, 1, 0);
				case Blocks.FACE_LEFT: return new Vector3f(-1, 0, 0);
				case Blocks.FACE_RIGHT: return new Vector3f(1, 0, 0);
				case Blocks.FACE_BACK: return new Vector3f(0, 0, -1);
				case Blocks.FACE_FRONT: return new Vector3f(0, 0, 1);
				default:
					return new Vector3f(0, 0, 0);
			}
		}
		
		public static FaceType getFromNormal(Vector3f normal) {
			normal = normal.normalize();
			if(normal.x >  0.7) return FaceType.east;
			if(normal.x < -0.7) return FaceType.west;
			if(normal.z >  0.7) return FaceType.north;
			if(normal.z < -0.7) return FaceType.south;
			if(normal.y >  0.7) return FaceType.up;
			if(normal.y < -0.7) return FaceType.down;
			return FaceType.none;
		}
		
//		public FaceType rotateXZ(int xstep, int zstep) {
//			return getFromNormal(normal()
//				.rotateX((float)(xstep * java.lang.Math.PI / 2.0f))
//				.rotateY((float)(zstep * java.lang.Math.PI / 2.0f))
//			);
//		}
		
		public int getFlags() {
			return flags;
		}

		public FaceType rotate(Matrix4f mat) {
			switch(flags) {
				case Blocks.FACE_DOWN: return FaceType.getFromNormal(new Vector3f(-mat.m10(), -mat.m11(), -mat.m12()));
				case Blocks.FACE_UP: return FaceType.getFromNormal(new Vector3f(mat.m10(), mat.m11(), mat.m12()));
				case Blocks.FACE_LEFT: return FaceType.getFromNormal(new Vector3f(-mat.m00(), -mat.m01(), -mat.m02()));
				case Blocks.FACE_RIGHT: return FaceType.getFromNormal(new Vector3f(mat.m00(), mat.m01(), mat.m02()));
				case Blocks.FACE_BACK: return FaceType.getFromNormal(new Vector3f(-mat.m20(), -mat.m21(), -mat.m22()));
				case Blocks.FACE_FRONT: return FaceType.getFromNormal(new Vector3f(mat.m20(), mat.m21(), mat.m22()));
				default: return this;
			}
			
//			return FaceType.getFromNormal(mat.transformDirection(normal()));
		}
	}
	
	public static enum Axis { x, y, z }
	
	static class JsonModelFace {
		public Vector4f uv = new Vector4f(0, 0, 16, 16);
		public String texture;
		public FaceType cullface = FaceType.none;
		public int tintindex;
		public float rotation = 0;
		
		@JsonSetter(value = "uv")
		public void setUv(List<Number> list) {
			uv = new Vector4f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue(),
				list.get(3).floatValue()
			);
		}
		
		@JsonSetter(value = "tintindex")
		public void setTintIndex(Number value) {
			tintindex = value.intValue();
		}
		
		@JsonSetter(value = "rotation")
		public void setRotation(Number value) {
			rotation = (float)Math.toRadians(value.floatValue());
		}

		public JsonModelFace build(JsonModelObject model) {
			JsonModelFace next = new JsonModelFace();
			next.cullface = cullface;
			next.tintindex = tintindex;
			next.rotation = rotation;
			if(texture.startsWith("#")) {
				next.texture = model.textures.get(texture.substring(1));
			} else {
				next.texture = texture;
			}
			
			Vector4f next_uv = new Vector4f();
			next.uv = uv.get(next_uv);
			
			if(rotation != 0) {
				float cx = (uv.x() + uv.z()) / 2.0f;
				float cy = (uv.y() + uv.w()) / 2.0f;
				Vector3f p1 = new Vector3f(uv.x() - cx, uv.y() - cy, 0);
				Vector3f p2 = new Vector3f(uv.z() - cx, uv.w() - cy, 0);
				p1.rotateZ(rotation);
				p2.rotateZ(rotation);
				next_uv.set(
					p1.x + cx,
					p1.y + cy,
					p2.x + cx,
					p2.y + cy
				);
			}
			
			return next;
		}
		
//		@Override
//		public String toString() {
//			return String.format("face{ texture=\"%s\" }", texture);
//		}
	}
	
	static class JsonRotation {
		public Vector3f origin = new Vector3f(8, 8, 8);
		public float angle;
		public Axis axis;
		public boolean rescale;
		
		@JsonSetter(value = "origin")
		public void setOrigin(List<Number> list) {
			origin = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@JsonSetter(value = "angle")
		public void setAngle(Number value) {
			angle = (float)Math.toRadians(value.floatValue());
		}
			
		public Vector3f apply(float[] vertex, int i) {
//			switch(axis) {
//				case x: Maths.fastRotateX(vertex, origin, i, angle); break;
//				case y: Maths.fastRotateY(vertex, origin, i, angle); break;
//				case z: Maths.fastRotateZ(vertex, origin, i, angle); break;
//			}
			Vector3f v = new Vector3f(vertex[i], vertex[i + 1], vertex[i + 2]).sub(origin);
			
			if(rescale) {
				float a = (float)(1.0 / Math.cos(angle));
				switch(axis) {
					case x: { // right
						v.mul(1, a, a);
						break;
					}
					case y: { // up
						v.mul(a, 1, a);
						break;
					}
					case z: { // front
						v.mul(a, a, 1);
						break;
					}
				}
			}
			
			switch(axis) {
				case x: v.rotateX(angle); break;
				case y: v.rotateY(angle); break;
				case z: v.rotateZ(angle); break;
			}
			v.add(origin);
			
			vertex[i] = v.x;
			vertex[i + 1] = v.y;
			vertex[i + 2] = v.z;
			return v;
		}
	}
	
	static class JsonModelElement {
		public Vector3f from = new Vector3f(0, 0, 0);
		public Vector3f to = new Vector3f(16, 16, 16);
		
		@JsonProperty(required = false)
		public boolean shade = false;
		
		@JsonProperty(required = false)
		private String __comment;
		
		@JsonProperty(required = false)
		public JsonRotation rotation;
		public Map<FaceType, JsonModelFace> faces;
		
		
		@JsonSetter(value = "from")
		public void setFrom(List<Number> list) {
			from = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@JsonSetter(value = "to")
		public void setTo(List<Number> list) {
			to = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@Override
		public String toString() {
			return String.format("element{ from=%s, to=%s, faces=%s }",
				from.toString(NumberFormat.getNumberInstance(Locale.US)),
				to.toString(NumberFormat.getNumberInstance(Locale.US)),
				faces
			);
		}

		public JsonModelElement build(JsonModelObject model) {
			JsonModelElement next = new JsonModelElement();
			next.from = this.from.get(new Vector3f());
			next.to = this.to.get(new Vector3f());
			next.shade = this.shade;
			
			if(!faces.isEmpty()) {
				next.faces = new HashMap<>();
				
				for(FaceType type : faces.keySet()) {
					JsonModelFace face = faces.get(type);
					next.faces.put(type, face.build(model));
				}
			}
			
			// Copy
			next.rotation = this.rotation;
			return next;
		}
	}
	
	static class JsonDisplayElement {
		public Vector3fc rotation = new Vector3f(0, 0, 0);
		public Vector3fc translation = new Vector3f(0, 0, 0);
		public Vector3fc scale = new Vector3f(1, 1, 1);
		
		@JsonSetter(value = "rotation")
		public void setRotation(List<Number> list) {
			rotation = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@JsonSetter(value = "translation")
		public void setTranslation(List<Number> list) {
			translation = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@JsonSetter(value = "scale")
		public void setScale(List<Number> list) {
			scale = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@Override
		public String toString() {
			return String.format("element{ rotation=%s, translation=%s, scale=%s }",
				rotation.toString(),
				translation.toString(),
				scale.toString()
			);
		}
		
	}
	
	static class JsonModelObject {
		@JsonProperty(required = false)
		private JsonModelObject parent;
		
		@JsonProperty(required = false)
		private List<JsonModelElement> elements;
		
		@JsonProperty(required = false)
		private Map<String, String> textures = Map.of();
		
		@JsonProperty(required = false)
		private Map<String, JsonDisplayElement> display = Map.of();

		@JsonProperty(required = false)
		private Boolean ambientocclusion;
		
		@JsonProperty(required = false)
		private String gui_light;
		
		private JsonModelObject(String path) throws Exception {
			mapper.readerForUpdating(this).readValue(factory.createParser(resource.getBlockModelBytes(path)));
		}
		
		private JsonModelObject() {}
		
		String getGuiLight() {
			if(gui_light != null) return gui_light;
			return (parent == null) ? "":parent.getGuiLight();
		}
		
		boolean getAmbientocclusion() {
			if(ambientocclusion != null) return ambientocclusion;
			return (parent == null) ? false:parent.getAmbientocclusion();
		}
		
		List<JsonModelElement> getModelElements() {
			if(elements != null) return elements;
			return (parent == null) ? List.of():parent.getModelElements();
		}
		
		Map<String, String> getResolvedTextures() {
			Map<String, String> map = new HashMap<>();
			map.putAll(textures);
			
			JsonModelObject parent = this.parent;
			while(parent != null) {
				Map<String, String> parent_textures = parent.textures;
				
				for(String key : parent_textures.keySet()) {
					String value = parent_textures.get(key);
					
					if(value.startsWith("#")) {
						value = map.get(value.substring(1));
					}
					
					map.put(key, value);
				}
				
				parent = parent.parent;
			}
			
			return map;
		}
		
		/**
		 * Take all the loaded data and compile it into one object.
		 */
		public JsonModelObject build() {
			JsonModelObject model = new JsonModelObject();
			model.gui_light = this.getGuiLight();
			model.ambientocclusion = this.getAmbientocclusion();
			model.textures = this.getResolvedTextures();
			
			{
				model.elements = new ArrayList<>();
				
				List<JsonModelElement> list = getModelElements();
				for(JsonModelElement element : list) {
					model.elements.add(element.build(model));
				}
			}
			
			return model;
		}
	}
	
	public static class FastModel {
		public static class ModelObject {
			public List<ModelElement> elements;
		}
		
		public static class ModelElement {
			public Map<FaceType, ModelFace> faces;
		}
		
		public static class ModelFace {
			public int textureId;
			public FaceType cullface;
			public float[] vertex;
			public float[] uv;
		}
		
		public static ModelObject createModelObject(JsonModelObject json) {
			ModelObject model = new ModelObject();
			model.elements = new ArrayList<>();
			for(JsonModelElement element : json.elements) {
				model.elements.add(createModelElement(element));
			}
			
			return model;
		}
		
		public static ModelElement createModelElement(JsonModelElement json) {
			ModelElement element = new ModelElement();
			element.faces = new HashMap<>();
			for(FaceType type : json.faces.keySet()) {
				ModelFace face = createModelFace(type, json, json.faces.get(type));
				if(json.rotation != null)
					applyRotation(face, json.rotation);
				
				element.faces.put(type, face);
			}
			
			return element;
		}
		
		private static void applyRotation(ModelFace face, JsonRotation rotation) {
			rotation.apply(face.vertex, 0);
			rotation.apply(face.vertex, 3);
			rotation.apply(face.vertex, 6);
			rotation.apply(face.vertex, 9);
			rotation.apply(face.vertex, 12);
			rotation.apply(face.vertex, 15);
			
//			switch(rotation.axis) {
//				case x: {
//					Maths.fastRotateX(face.vertex, rotation.origin, 0, rotation.angle);
//					Maths.fastRotateX(face.vertex, rotation.origin, 3, rotation.angle);
//					Maths.fastRotateX(face.vertex, rotation.origin, 6, rotation.angle);
//					Maths.fastRotateX(face.vertex, rotation.origin, 9, rotation.angle);
//					Maths.fastRotateX(face.vertex, rotation.origin, 12, rotation.angle);
//					Maths.fastRotateX(face.vertex, rotation.origin, 15, rotation.angle);
//					break;
//				}
//				case y: {
//					Maths.fastRotateY(face.vertex, rotation.origin, 0, rotation.angle);
//					Maths.fastRotateY(face.vertex, rotation.origin, 3, rotation.angle);
//					Maths.fastRotateY(face.vertex, rotation.origin, 6, rotation.angle);
//					Maths.fastRotateY(face.vertex, rotation.origin, 9, rotation.angle);
//					Maths.fastRotateY(face.vertex, rotation.origin, 12, rotation.angle);
//					Maths.fastRotateY(face.vertex, rotation.origin, 15, rotation.angle);
//					break;
//				}
//				case z: {
//					Maths.fastRotateZ(face.vertex, rotation.origin, 0, rotation.angle);
//					Maths.fastRotateZ(face.vertex, rotation.origin, 3, rotation.angle);
//					Maths.fastRotateZ(face.vertex, rotation.origin, 6, rotation.angle);
//					Maths.fastRotateZ(face.vertex, rotation.origin, 9, rotation.angle);
//					Maths.fastRotateZ(face.vertex, rotation.origin, 12, rotation.angle);
//					Maths.fastRotateZ(face.vertex, rotation.origin, 15, rotation.angle);
//					break;
//				}
//			}
		}
		
		// 0.54
		private static ModelFace createModelFace(FaceType type, JsonModelElement element, JsonModelFace json) {
			ModelFace face = new ModelFace();
			
			// 0.9ms
			int id = LwjglRender.atlas.addTexture(json.texture, resource.readBufferedImage(json.texture));
			
			face.textureId = id;
			face.vertex = Maths.getModelVertexes(type, element.from, element.to);
			Vector4f uv = json.uv;
			face.uv = new float[] {
				uv.x, uv.w, uv.z, uv.w, uv.z, uv.y,
				uv.x, uv.w, uv.z, uv.y, uv.x, uv.y
			};
			LwjglRender.atlas.transformModelUv(id, face.uv);
			
			return face;
		}
	}
	
	public static VersionResourceReader resource;
	private static Map<String, FastModel.ModelObject> cache_modles = new HashMap<>();
	public static FastModel.ModelObject loadModel(String path) {
		FastModel.ModelObject model = cache_modles.get(path);
		if(model != null) return model;
		
		try {
			JsonModelObject loaded = new JsonModelObject(path).build();
			
			// Average: 7,77621525 ms
			TimerUtils.begin();
			FastModel.ModelObject object = FastModel.createModelObject(loaded);
			TimerUtils.end();
			cache_modles.put(path, object);
			return object;
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
	}
}
