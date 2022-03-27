package com.hardcoded.render.generator;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.util.*;

import org.joml.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;
import com.hardcoded.lwjgl.data.TextureAtlasMipmap;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.constants.Axis;
import com.hardcoded.mc.constants.Direction;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.*;
import com.hardcoded.util.MathUtils;
import com.hardcoded.util.Maths;
import com.hardcoded.util.math.box.BoxFace;
import com.hardcoded.util.math.box.BoxShape;

public class FastModelJsonLoader {
	private static final JsonFactory factory = new JsonFactory();
	private static final ObjectMapper mapper = new ObjectMapper();
	
	static class JsonModelFace {
		@JsonIgnore
		private boolean was_uv_defined;
		
		public float[] built_uv;
		
		/**
		 * Uv coordinates in block space
		 */
		public Vector4f block_uv = new Vector4f(0, 0, 16, 16);
		public String texture;
		public Direction cullface;
		public int tintindex = Integer.MIN_VALUE;
		public int rotation = 0;
		
		@JsonSetter(value = "uv")
		public void setUv(List<Number> list) {
			block_uv = new Vector4f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue(),
				list.get(3).floatValue()
			);
			was_uv_defined = true;
		}
		
		@JsonSetter(value = "cullface")
		public void setCullface(String face) {
			cullface = Direction.get(face);
		}
		
		@JsonSetter(value = "tintindex")
		public void setTintIndex(Number value) {
			tintindex = value.intValue();
		}
		
		@JsonSetter(value = "rotation")
		public void setRotation(Number value) {
			rotation = value.intValue();
		}

		public JsonModelFace build(Direction face, JsonModelElement element, JsonModelObject model) {
			JsonModelFace next = new JsonModelFace();
			next.cullface = cullface;
			next.tintindex = tintindex;
			next.rotation = rotation;
			if(texture.startsWith("#")) {
				next.texture = model.textures.get(texture.substring(1));
			} else {
				next.texture = texture;
			}
			
			if(next.texture == null) {
				// Replace all textures that are undefined with the default 'projectedit:missing' texture
				next.texture = "projectedit:missing";
			}
			
			if(!was_uv_defined) {
				block_uv = Maths.generateUv(face, element.from, element.to);
				
				if(face.getAxis() != Axis.y) {
					block_uv.w = 16 - block_uv.w;
					block_uv.y = 16 - block_uv.y;
					next.built_uv = rotateUvTest(block_uv, (rotation + 180) % 360);
				} else {
//					next.built_uv = rotateUvTest(block_uv, rotation);
					next.built_uv = rotateUvTest(block_uv, rotation);
				}
				
			} else {
				next.built_uv = rotateUvTest(block_uv, rotation);
			}
			
			next.was_uv_defined = was_uv_defined;
			next.block_uv = block_uv.get(new Vector4f());
			
			return next;
		}
	}
	
	public static float[] rotateUvTest(Vector4f uv, int rotation) {
		switch(rotation) {
			default:
			case 0: {
				return new float[] {
					uv.x, uv.w, // 0
					uv.z, uv.w, // 1
					uv.z, uv.y, // 2
					
					uv.x, uv.w, // 0
					uv.z, uv.y, // 2
					uv.x, uv.y, // 3
				};
			}
			case 90: {
				return new float[] {
					uv.z, uv.w, // 1
					uv.z, uv.y, // 2
					uv.x, uv.y, // 3
					
					uv.z, uv.w, // 1
					uv.x, uv.y, // 3
					uv.x, uv.w, // 0
				};
			}
			case 180: {
				return new float[] {
					uv.z, uv.y, // 2
					uv.x, uv.y, // 3
					uv.x, uv.w, // 0
					
					uv.z, uv.y, // 2
					uv.x, uv.w, // 0
					uv.z, uv.w, // 1
				};
			}
			case 270: {
				return new float[] {
					uv.x, uv.y, // 3
					uv.x, uv.w, // 0
					uv.z, uv.w, // 1
					
					uv.x, uv.y, // 3
					uv.z, uv.w, // 1
					uv.z, uv.y, // 2
				};
			}
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
		
		@JsonSetter(value = "angle")
		public void setAngle(Number value) {
			angle = (float)Math.toRadians(value.floatValue());
		}
			
		public Vector3f apply(float[] vertex, int i) {
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

	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JsonModelElement {
		public Vector3f from = new Vector3f(0, 0, 0);
		public Vector3f to = new Vector3f(16, 16, 16);
		
		@JsonProperty(required = false)
		// This is true by default
		public boolean shade = true;
		
		@JsonProperty(required = false)
		public JsonRotation rotation;
		
		public Map<Direction, JsonModelFace> faces = Map.of();
		
		@JsonSetter(value = "from")
		public void setFrom(List<Number> list) {
			from = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		@JsonSetter(value = "faces")
		public void setFaces(Map<String, JsonModelFace> map) {
			faces = new HashMap<>();
			
			for(String key : map.keySet()) {
				Direction face = Direction.get(key);
				faces.put(face, map.get(key));
			}
		}
		
		@JsonSetter(value = "to")
		public void setTo(List<Number> list) {
			to = new Vector3f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue()
			);
		}
		
		public JsonModelElement build(JsonModelObject model) {
			JsonModelElement next = new JsonModelElement();
			next.from = this.from.get(new Vector3f());
			next.to = this.to.get(new Vector3f());
			next.shade = this.shade;
			
			if(!faces.isEmpty()) {
				next.faces = new HashMap<>();
				
				for(Direction type : faces.keySet()) {
					JsonModelFace face = faces.get(type);
					next.faces.put(type, face.build(type, this, model));
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
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
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
		private Boolean ambientocclusion = true; 
		
		@JsonProperty(required = false)
		private String gui_light;
		
		private JsonModelObject(String path) throws Exception {
			byte[] bytes = resource.getBlockModelBytes(path);
			
			if(bytes != null && bytes.length > 0) {
				mapper.readerForUpdating(this).readValue(factory.createParser(bytes));
			}
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
					String value = textures.get(key);
					if(value == null) {
						value = parent_textures.get(key);
					}
					
					if(value.startsWith("#")) {
						value = map.get(value.substring(1));
					}
					
					map.put(key, value);
				}
				
				parent = parent.parent;
			}
			
			for(String key : textures.keySet()) {
				String value = textures.get(key);
				
				if(!value.startsWith("#")) {
					// Override parent value
					map.put(key, value);
				}
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
	
	private static float[] transformCopy(float[] vertex, Matrix4f mat) {
		Matrix4f pre = new Matrix4f()
			.scale(1 / 16.0f)
			.mul(mat);
		
		float[] array = new float[vertex.length];
		for(int i = 0, len = vertex.length; i < len; i += 3) {
			Vector3f v = pre.transformPosition(new Vector3f(vertex[i], vertex[i + 1], vertex[i + 2]));
			array[i] = v.x;
			array[i + 1] = v.y;
			array[i + 2] = v.z;
		}
		
		return array;
	}
	
	private static float[] rotateUvlock(Direction face_type, ModelFace face) {
		float[] va = face.vertex;
		Vector3f p0 = new Vector3f(va[0], va[1], va[2]).mul(16);
		Vector3f p1 = new Vector3f(va[3], va[4], va[5]).mul(16);
		Vector3f p2 = new Vector3f(va[6], va[7], va[8]).mul(16);
		Vector3f p3 = new Vector3f(va[15], va[16], va[17]).mul(16);

		TextureAtlas atlas = ProjectEdit.getInstance().getTextureManager().getBlockAtlas().getMain();
		int atlas_width = atlas.getWidth();
		int atlas_height = atlas.getHeight();
		AtlasUv atlas_uv = atlas.getUv(face.textureId);
		
		int wi = (int)Math.ceil((atlas_uv.x1 - atlas_uv.x0) * atlas_width); // 16;
		int he = (int)Math.ceil((atlas_uv.y1 - atlas_uv.y0) * atlas_height); // 16;
		
		float[] next_uv;
		switch(face_type.getFlags()) {
			default:
			case Direction.FACE_UP: {
				next_uv = new float[] {
					p0.x, p0.z, // 0
					p1.x, p1.z, // 1
					p2.x, p2.z, // 2
					
					p0.x, p0.z, // 0
					p2.x, p2.z, // 2
					p3.x, p3.z, // 3
				};
				break;
			}
			case Direction.FACE_DOWN: {
				next_uv = new float[] {
					p0.x, he - p0.z, // 0
					p1.x, he - p1.z, // 1
					p2.x, he - p2.z, // 2
					
					p0.x, he - p0.z, // 0
					p2.x, he - p2.z, // 2
					p3.x, he - p3.z, // 3
				};
				break;
			}
			case Direction.FACE_FRONT: {
				next_uv = new float[] {
					wi - p0.x, he - p0.y, // 0
					wi - p1.x, he - p1.y, // 1
					wi - p2.x, he - p2.y, // 2
					
					wi - p0.x, he - p0.y, // 0
					wi - p2.x, he - p2.y, // 2
					wi - p3.x, he - p3.y, // 3
				};
				break;
			}
			case Direction.FACE_BACK: {
				next_uv = new float[] {
					p0.x, he - p0.y, // 0
					p1.x, he - p1.y, // 1
					p2.x, he - p2.y, // 2
					
					p0.x, he - p0.y, // 0
					p2.x, he - p2.y, // 2
					p3.x, he - p3.y, // 3
				};
				break;
			}
			case Direction.FACE_RIGHT: {
				next_uv = new float[] {
					wi - p0.z, he - p0.y, // 0
					wi - p1.z, he - p1.y, // 1
					wi - p2.z, he - p2.y, // 2
					
					wi - p0.z, he - p0.y, // 0
					wi - p2.z, he - p2.y, // 2
					wi - p3.z, he - p3.y, // 3
				};
				break;
			}
			case Direction.FACE_LEFT: {
				next_uv = new float[] {
					p0.z, he - p0.y, // 0
					p1.z, he - p1.y, // 1
					p2.z, he - p2.y, // 2
					
					p0.z, he - p0.y, // 0
					p2.z, he - p2.y, // 2
					p3.z, he - p3.y, // 3
				};
				break;
			}
		}
		
		ProjectEdit.getInstance().getTextureManager().getBlockAtlas().transformModelUv(face.textureId, next_uv);
		return next_uv;
	}
	
	private static List<ModelElement> bakeDelegate(ModelObject base, ModelDelegate model) {
		List<ModelElement> list = new ArrayList<>();
		if(base.elements == null) {
			return list;
		}
		
		Matrix4f mat = model.getTranslationMatrix().get(new Matrix4f());
		boolean uvlock = model.isUvLocked();
		
		for(ModelElement model_element : base.elements) {
			ModelElement element = new ModelElement();
			element.hasNon90Rotation = model_element.hasNon90Rotation;
			element.faces = new HashMap<>();
			
			for(Map.Entry<Direction, ModelFace> entry : model_element.faces.entrySet()) {
				Direction model_face_type = entry.getKey();
				ModelFace model_face = entry.getValue();
				
				ModelFace face = new ModelFace();
				face.tintIndex = model_face.tintIndex;
				
				Direction face_type = model_face_type.rotate(mat);
				if(model_face.cullface != null) {
					face.cullface = model_face.cullface.rotate(mat);
				}
				
				face.textureId = model_face.textureId;
				face.vertex = transformCopy(model_face.vertex, mat);
				if(uvlock) {
					face.uv = rotateUvlock(face_type, face);
				} else {
					face.uv = model_face.uv.clone();
				}
				
				face.box_face = model_face.box_face;
				element.faces.put(face_type, face);
			}
			
			list.add(element);
		}
		
		for(BoxShape model_shape : base.shapes) {
			BoxShape shape = new BoxShape(model_shape.from, model_shape.to);
			mat.transformPosition(shape.from);
			mat.transformPosition(shape.to);
			
			if(shape.from.x > shape.to.x) {
				float tmp = shape.from.x;
				shape.from.x = shape.to.x;
				shape.to.x = tmp;
			}
			
			if(shape.from.y > shape.to.y) {
				float tmp = shape.from.y;
				shape.from.y = shape.to.y;
				shape.to.y = tmp;
			}
			
			if(shape.from.z > shape.to.z) {
				float tmp = shape.from.z;
				shape.from.z = shape.to.z;
				shape.to.z = tmp;
			}
			
			model.shapes.add(shape);
		}
		
		return list;
	}
	
	public static class FastModel {
		private static final Matrix4f def_tm = new Matrix4f();
		
		public static class ModelDelegate extends ModelObject {
			protected final List<ModelElement> baked;
			protected final Matrix4f matrix;
			protected final boolean uvlocked;
			protected final List<BoxShape> shapes;
			
			public ModelDelegate(Matrix4f matrix, boolean uvlocked, ModelObject base) {
				this.matrix = matrix;
				this.uvlocked = uvlocked;
				this.shapes = new ArrayList<>();
				this.baked = bakeDelegate(base, this);
			}
			
			@Override
			public List<ModelElement> getElements() {
				return baked;
			}
			
			@Override
			public boolean hasElements() {
				return !baked.isEmpty();
			}
			
			@Override
			public List<BoxShape> getShapes() {
				return shapes;
			}
			
			@Override
			public boolean isUvLocked() {
				return uvlocked;
			}

			@Override
			public Matrix4f getTranslationMatrix() {
				return matrix;
			}
		}
		
		public static class ModelObject {
			private List<BoxShape> shapes = new ArrayList<>();
			private List<ModelElement> elements;
			
			public List<ModelElement> getElements() {
				return elements;
			}
			
			// Used for face culling
			public List<BoxShape> getShapes() {
				return shapes;
			}
			
			public boolean isUvLocked() {
				return false;
			}
			
			public boolean hasElements() {
				return !elements.isEmpty();
			}
			
			public Matrix4f getTranslationMatrix() {
				return def_tm;
			}
		}
		
		public static class ModelElement {
			public Map<Direction, ModelFace> faces;
			public boolean hasNon90Rotation;
		}
		
		public static class ModelFace {
			public int textureId;
			public int tintIndex;
			public Direction cullface;
			public float[] vertex;
			public float[] uv;
			
			public BoxFace box_face;
		}
		
		public static ModelObject createModelObject(JsonModelObject json) {
			ModelObject model = new ModelObject();
			model.elements = new ArrayList<>();
			for(JsonModelElement json_element : json.elements) {
				ModelElement element = createModelElement(json_element);
				model.elements.add(element);
				
				if(!element.hasNon90Rotation) {
					//model.shapes.add(new BoxShape(json_element.from, json_element.to));
					model.shapes.add(new BoxShape(json_element.from, json_element.to));
				}
			}
			
			return model;
		}
		
		public static ModelElement createModelElement(JsonModelElement json) {
			ModelElement element = new ModelElement();
			element.faces = new HashMap<>();
			for(Direction type : json.faces.keySet()) {
				ModelFace face = createModelFace(type, json, json.faces.get(type));
				if(json.rotation != null)
					applyRotation(face, json.rotation);
				
				element.faces.put(type, face);
			}
			
			if(json.rotation != null) {
				float angle = json.rotation.angle % 90;
				element.hasNon90Rotation = !MathUtils.fuzzyEquals(angle, 0, 0.01);
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
		}
		
		private static ModelFace createModelFace(Direction type, JsonModelElement element, JsonModelFace json) {
			ModelFace face = new ModelFace();
			
			TextureAtlasMipmap atlas = ProjectEdit.getInstance().getTextureManager().getBlockAtlas();
			
			int id = atlas.getImageId(json.texture);
			if(id < 0) {
				BufferedImage image = resource.readBufferedImage(json.texture);
				if(image != null) {
					// XXX: Replace this with a more controlled code
					if(json.texture.contains("leaves")) {
						int width = image.getWidth();
						int height = image.getHeight();
						
						BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
						Graphics2D gr = copy.createGraphics();
						gr.drawImage(image, 0, 0, null);
						gr.dispose();
						
						image = copy;
					}
					
					id = atlas.addTexture(json.texture, image);
				} else {
					// TODO: Add a constant value for the missing texture
					id = atlas.getImageId("projectedit:missing");
				}
			}
			
			face.tintIndex = json.tintindex;
			face.textureId = id;
			face.cullface = json.cullface;
			face.vertex = Maths.getModelVertexes(type, element.from, element.to);
			
			face.uv = json.built_uv.clone();
			atlas.transformModelUv(id, face.uv);
			
			// face.vertex
			// (x0, y0), ., (x1, y1)
			{
				Direction direction = type;
				Vector3f from = element.from;
				Vector3f to = element.to;
				
				AtlasUv uv = atlas.getUv(id);
				switch(type) {
					case SOUTH: // back
					case NORTH: // front
						face.box_face = new BoxFace(from.x, from.y, to.x, to.y, uv, direction.getAxisDirection() < 0 ? from.z:to.z, direction);
						break;
					case EAST: // right
					case WEST: // left
						face.box_face = new BoxFace(from.z, from.y, to.z, to.y, uv, direction.getAxisDirection() < 0 ? from.x:to.x, direction);
						break;
					case UP: // up
					case DOWN: // down
						face.box_face = new BoxFace(from.x, from.z, to.x, to.z, uv, direction.getAxisDirection() < 0 ? from.y:to.y, direction);
						break;
				}
			}
			
			return face;
		}
	}
	
	private static VersionResourceReader resource;
	public static void setResourceReader(VersionResourceReader resource) {
		FastModelJsonLoader.resource = resource;
	}
	
	private static final Map<String, FastModel.ModelObject> cached_delegates = new HashMap<>();
	public static FastModel.ModelObject loadModelDelegate(String path, int xAxis, int yAxis, boolean uvlock) {
		FastModel.ModelObject model = loadModel(path);
		String generated_path = String.format("%s_%d_%d_%s", path, xAxis, yAxis, uvlock);
		
		ModelObject object = cached_delegates.get(generated_path);
		if(object == null) {
			Matrix4f matrix = new Matrix4f();
			{
				matrix.translateLocal(-8, -8, -8)
					.rotateLocalX(MathUtils.toRadians(-xAxis))
					.rotateLocalY(MathUtils.toRadians(-yAxis))
					.translateLocal(8, 8, 8);
			}
			
			object = new ModelDelegate(matrix, uvlock, model);
			cached_delegates.put(generated_path, object);
		}
		
		return object;
	}
	
	private static final Map<String, FastModel.ModelObject> cache_models = new HashMap<>();
	public static FastModel.ModelObject loadModel(String path) {
		FastModel.ModelObject model = cache_models.get(path);
		if(model != null) return model;
		
		try {
			JsonModelObject loaded = new JsonModelObject(path).build();
			FastModel.ModelObject object = FastModel.createModelObject(loaded);
			cache_models.put(path, object);
			return object;
		} catch(Exception e) {
			e.printStackTrace();
			FastModel.ModelObject object = getMissingBlockModel();
			cache_models.put(path, object);
			return object;
		}
	}
	
	public static FastModel.ModelObject getMissingBlockModel() {
		FastModel.ModelObject model = cache_models.get("projectedit:model/missing");
		if(model != null) return model;
		
		try {
			JsonModelObject loaded = new JsonModelObject();
			mapper.readerForUpdating(loaded).readValue(factory.createParser("{\"parent\":\"minecraft:block/cube_all\",\"textures\":{\"all\":\"projectedit:missing\"}}"));
			
			FastModel.ModelObject object = FastModel.createModelObject(loaded.build());
			ModelDelegate delegate = new ModelDelegate(new Matrix4f(), false, object);
			cache_models.put("projectedit:model/missing", delegate);
			return object;
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
	}
	
	public static void unloadCache() {
		cache_models.clear();
		cached_delegates.clear();
	}
}
