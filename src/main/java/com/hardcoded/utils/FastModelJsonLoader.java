package com.hardcoded.utils;

import java.text.NumberFormat;
import java.util.*;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardcoded.lwjgl.data.Texture;

public class FastModelJsonLoader {
	public static enum FaceType { down, up, north, south, west, east, none }
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
			rotation = value.floatValue();
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
			next.uv = uv.get(new Vector4f());
			return next;
		}
		
		@Override
		public String toString() {
			return String.format("face{ texture=\"%s\" }", texture);
		}
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
		public Vector3f rotation = new Vector3f(0, 0, 0);
		public Vector3f translation = new Vector3f(0, 0, 0);
		public Vector3f scale = new Vector3f(1, 1, 1);
		
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
				rotation.toString(NumberFormat.getNumberInstance(Locale.US)),
				translation.toString(NumberFormat.getNumberInstance(Locale.US)),
				scale.toString(NumberFormat.getNumberInstance(Locale.US))
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
		
		private boolean ambientocclusion = false;
		
		@JsonProperty(required = false)
		private String gui_light;
		
		public JsonModelObject(String path) throws Exception {
			String content = resource.getBlockModel(path).toString();
//			System.out.println("-----------------------");
//			System.out.println("path: " + path);
//			System.out.println("content: " + content);
			
			JsonFactory factory = new JsonFactory();
			JsonParser parser = factory.createParser(content);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
			mapper.readerForUpdating(this).readValue(parser);
		}
		
		public JsonModelObject() {
			
		}
		
		public boolean getAmbientocclusion() {
			return ambientocclusion;
		}
		
		public String getGuiLight() {
			// side
			return gui_light;
		}
		
		public String resolveTexture(String name) {
			if(name.startsWith("#")) {
				String value = textures.get(name);
				if(value == null && parent != null) {
					return parent.resolveTexture(name);
				}
				
				return value;
			}
			
			return name;
		}
		 
		public List<JsonModelElement> getModelElements() {
			if(elements != null) {
				return elements;
			}
			
			if(parent != null) {
				return parent.getModelElements();
			}
			
			return List.of();
		}
		
		public Map<String, String> getResolvedTextures() {
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
			model.ambientocclusion = this.ambientocclusion;
			model.gui_light = this.gui_light;
			model.textures = this.getResolvedTextures();
			
			{
				model.elements = new ArrayList<>();
				
				List<JsonModelElement> list = getModelElements();
				for(JsonModelElement element : list) {
					model.elements.add(element.build(model));
				}
			}
			
//			System.out.println(model.textures);
//			System.out.println(model);
//			
//			for(JsonModelElement element : model.elements) {
//				System.out.println("    " + element);
//			}
			
			return model;
		}
		
		@Override
		public String toString() {
			return String.format("model=[\n\telements=%s\n\ttextures=%s\n];", getModelElements(), textures);
		}
	}
	
	private static VersionResourceReader resource;
	
	public static class FastModel {
		public static class ModelObject {
			public List<ModelElement> elements;
		}
		
		public static class ModelElement {
			public Map<FaceType, ModelFace> faces;
		}
		
		public static class ModelFace {
			public Texture texture;
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
				element.faces.put(type, createModelFace(type, json, json.faces.get(type)));
			}
			return element;
		}
		
		private static ModelFace createModelFace(FaceType type, JsonModelElement element, JsonModelFace json) {
			ModelFace face = new ModelFace();
			
			face.texture = resource.readTexture(json.texture);
			face.vertex = Maths.getModelVertexes(type, element.from, element.to);
			face.uv = Maths.getModelUvs(json.uv, face.texture);
			
			return face;
		}
	}
	
	/**
	 * Put compiled models inside this hash
	 */
	private static Map<String, FastModel.ModelObject> cache_modles = new HashMap<>();

	public static FastModel.ModelObject test(VersionResourceReader resource, String path) {
		FastModel.ModelObject model = cache_modles.get(path);
		if(model != null) return model;
		
		FastModelJsonLoader.resource = resource;
		
		try {
			JsonModelObject loaded = new JsonModelObject(path).build();
			FastModel.ModelObject object = FastModel.createModelObject(loaded);
			cache_modles.put(path, object);
			return object;
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
	}
	
	
}
