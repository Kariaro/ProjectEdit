package com.hardcoded.utils;

import java.awt.image.BufferedImage;
import java.lang.Math;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.joml.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.constants.Direction;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelDelegate;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;

public class FastModelJsonLoader {
	private static final JsonFactory factory = new JsonFactory();
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static enum Axis { x, y, z }
	public static enum TestXY {

		// We want to know how much each face has rotated.
		//    X    Y        +Z +Y +X -Z -Y -X
		// (  0,   0)  ->  ( 0  0  0  0  0  0 )
		// ( 90,   0)  ->  ( 0  0  1  2  2  3 )
		// (180,   0)  ->  ( 2  0  2  2  0  2 )
		// (270,   0)  ->  ( 0  2  3  2  0  1 )
		// (  0,  90)  ->  ( 0  1  0  0  3  0 )
		// ( 90,  90)  ->  ( 1  1  2  3  1  0 )
		// (180,  90)  ->  ( 2  1  2  2  3  2 )
		// (270,  90)  ->  ( 3  3  2  1  3  0 )
		// (  0, 180)  ->  ( 0  2  0  0  2  0 )
		// ( 90, 180)  ->  ( 2  2  3  0  0  1 )
		// (180, 180)  ->  ( 2  2  2  2  2  2 )
		// (270, 180)  ->  ( 2  0  1  0  2  3 )
		// (  0, 270)  ->  ( 0  3  0  0  1  0 )
		// ( 90, 270)  ->  ( 3  3  0  1  3  2 )
		// (180, 270)  ->  ( 2  3  2  2  1  2 )
		// (270, 270)  ->  ( 1  1  0  3  1  2 )
		

		// (  0,   0)  ->  ( 0  0  0  0  0  0 )
		// ( 90,   0)  ->  ( 0  0  1  2  2  3 )
		// (180,   0)  ->  ( 2  0  2  2  0  2 )
		// (270,   0)  ->  ( 0  2  3  2  0  1 )
		ROT_0_0(0, 0			, 0, 0, 0, 0, 0, 0),
		ROT_90_0(90, 0			, 0, 2, 3, 2, 0, 1),
		ROT_180_0(180, 0		, 2, 0, 2, 2, 0, 2),
		ROT_270_0(270, 0		, 0, 0, 1, 2, 2, 3),
		
		// (  0,  90)  ->  ( 0  1  0  0  3  0 )
		// ( 90,  90)  ->  ( 1  1  2  3  1  0 )
		// (180,  90)  ->  ( 2  1  2  2  3  2 )
		// (270,  90)  ->  ( 3  3  2  1  3  0 )
		ROT_0_90(0, 90			, 0, 3, 0, 0, 1, 0),
		ROT_90_90(90, 90		, 3, 3, 2, 1, 3, 0),
		ROT_180_90(180, 90		, 2, 3, 2, 2, 1, 2),
		ROT_270_90(270, 90		, 1, 1, 2, 3, 1, 0),

		// (  0, 180)  ->  ( 0  2  0  0  2  0 )
		// ( 90, 180)  ->  ( 2  2  3  0  0  1 )
		// (180, 180)  ->  ( 2  2  2  2  2  2 )
		// (270, 180)  ->  ( 2  0  1  0  2  3 )
		ROT_0_180(0, 180		, 0, 2, 0, 0, 2, 0),
		ROT_90_180(90, 180		, 2, 2, 1, 2, 0, 3),
		ROT_180_180(180, 180	, 0, 2, 2, 0, 2, 2),
		ROT_270_180(270, 180	, 2, 0, 3, 0, 2, 1),

		// (  0, 270)  ->  ( 0  3  0  0  1  0 )
		// ( 90, 270)  ->  ( 3  3  0  1  3  2 )
		// (180, 270)  ->  ( 2  3  2  2  1  2 )
		// (270, 270)  ->  ( 1  1  0  3  1  2 )
		ROT_0_270(0, 270		, 0, 1, 0, 0, 3, 0),
		ROT_90_270(90, 270		, 1, 1, 0, 3, 1, 2),
		ROT_180_270(180, 270	, 2, 1, 2, 2, 3, 2),
		ROT_270_270(270, 270	, 3, 3, 0, 1, 3, 2),
		;
		
		private static final Map<Integer, TestXY> INDEX_TO_ROT = Arrays.stream(values()).collect(Collectors.toMap(TestXY::getIndex, (x) -> x));
		
		// We want to know how much each face has rotated.
		//    X    Y        +Z +Y +X -Z -Y -X
		// (  0,   0)  ->  ( 0  0  0  0  0  0 )
		// ( 90,   0)  ->  ( 0  0  1  2  2  3 )
		// (180,   0)  ->  ( 2  0  2  2  0  2 )
		// (270,   0)  ->  ( 0  2  3  2  0  1 )
		// (  0,  90)  ->  ( 0  1  0  0  3  0 )
		// ( 90,  90)  ->  ( 1  1  2  3  1  0 )
		// (180,  90)  ->  ( 2  1  2  2  3  2 )
		// (270,  90)  ->  ( 3  3  2  1  3  0 )
		// (  0, 180)  ->  ( 0  2  0  0  2  0 )
		// ( 90, 180)  ->  ( 2  2  3  0  0  1 )
		// (180, 180)  ->  ( 2  2  2  2  2  2 )
		// (270, 180)  ->  ( 2  0  1  0  2  3 )
		// (  0, 270)  ->  ( 0  3  0  0  1  0 )
		// ( 90, 270)  ->  ( 3  3  0  1  3  2 )
		// (180, 270)  ->  ( 2  3  2  2  1  2 )
		// (270, 270)  ->  ( 1  1  0  3  1  2 )
		
		private final int rot_x;
		private final int rot_y;
		
		public final int xp;
		public final int yp;
		public final int zp;
		public final int xm;
		public final int ym;
		public final int zm;
		private TestXY(int x, int y, int zp, int yp, int xp, int zm, int ym, int xm) {
			this.rot_x = x;
			this.rot_y = y;
			this.xp = xp * 90;
			this.yp = yp * 90;
			this.zp = zp * 90;
			this.xm = xm * 90;
			this.ym = ym * 90;
			this.zm = zm * 90;
		}
		
		public int getIndex() {
			return rot_x * 360 + rot_y;
		}
		
		public int getTextureRotation(FaceType face) {
			switch(face.getFlags()) {
				case Direction.FACE_FRONT: return zp;
				case Direction.FACE_BACK: return zm;
				case Direction.FACE_UP: return yp;
				case Direction.FACE_DOWN: return ym;
				case Direction.FACE_RIGHT: return xp;
				case Direction.FACE_LEFT: return xm;
			}
			
			return 0;
		}
		
		public static TestXY testGet(int x, int y) {
			return INDEX_TO_ROT.get(x * 360 + y);
		}
	}
	
	public static enum FaceType {
		east(Direction.FACE_RIGHT, Axis.x, new Vector3f(1, 0, 0)),
		west(Direction.FACE_LEFT, Axis.x, new Vector3f(-1, 0, 0)),
		up(Direction.FACE_UP, Axis.y, new Vector3f(0, 1, 0)),
		down(Direction.FACE_DOWN, Axis.y, new Vector3f(0, -1, 0)),
		north(Direction.FACE_FRONT, Axis.z, new Vector3f(0, 0, -1)),
		south(Direction.FACE_BACK, Axis.z, new Vector3f(0, 0, 1)),
		;
		
		public static final FaceType[] FACES = FaceType.values();
		
		private final int flags;
		private final Vector3fc normal;
		private final Axis axis;
		private FaceType(int flags, Axis axis, Vector3f normal) {
			this.flags = flags;
			this.normal = normal;
			this.axis = axis;
		}
		
		public Vector3f getNormal() {
			return normal.get(new Vector3f());
		}
		
		public Axis getAxis() {
			return axis;
		}
		
		public static FaceType getFromNormal(Vector3f normal) {
			normal = normal.normalize();
			float x = normal.x;
			float y = normal.y;
			float z = normal.z;
			if(x >  0.7) return FaceType.east;
			if(x < -0.7) return FaceType.west;
			if(y >  0.7) return FaceType.up;
			if(y < -0.7) return FaceType.down;
			if(z >  0.7) return FaceType.south;
			if(z < -0.7) return FaceType.north;
			throw new UnsupportedOperationException("Invalid direction");
		}
		
		public static FaceType getFromNormal(float x, float y, float z) {
			float div = 1.0f / (float)Math.sqrt(x*x + y*y + z*z);
			x *= div; y *= div; z *= div;
			if(x >  0.7) return FaceType.east;
			if(x < -0.7) return FaceType.west;
			if(y >  0.7) return FaceType.up;
			if(y < -0.7) return FaceType.down;
			if(z >  0.7) return FaceType.south;
			if(z < -0.7) return FaceType.north;
			throw new UnsupportedOperationException("Invalid direction");
		}
		
		public int getFlags() {
			return flags;
		}

		public FaceType rotate(Matrix4f mat) {
			switch(flags) {
				case Direction.FACE_RIGHT: return FaceType.getFromNormal(mat.m00(), mat.m01(), mat.m02());
				case Direction.FACE_LEFT: return FaceType.getFromNormal(-mat.m00(), -mat.m01(), -mat.m02());
				case Direction.FACE_UP: return FaceType.getFromNormal(mat.m10(), mat.m11(), mat.m12());
				case Direction.FACE_DOWN: return FaceType.getFromNormal(-mat.m10(), -mat.m11(), -mat.m12());
				case Direction.FACE_FRONT: return FaceType.getFromNormal(-mat.m20(), -mat.m21(), -mat.m22());
				case Direction.FACE_BACK: return FaceType.getFromNormal(mat.m20(), mat.m21(), mat.m22());
				default: return this;
			}
		}

		public static FaceType get(String face) {
			switch(face.toLowerCase()) {
				case "up":
				case "top":
					return FaceType.up;
				case "bottom":
				case "down":
					return FaceType.down;
				case "right":
				case "east":
					return FaceType.east;
				case "left":
				case "west":
					return FaceType.west;
				case "front":
				case "north":
					return FaceType.north;
				case "back":
				case "south":
					return FaceType.south;
			}
			
			return null;
		}
	}
	
	static class JsonModelFace {
		@JsonIgnore
		private boolean was_uv_defined;
		
		public float[] built_uv;
		public Vector4f uv = new Vector4f(0, 0, 16, 16);
		public String texture;
		public FaceType cullface;
		public int tintindex;
		public int rotation = 0;
		
		@JsonSetter(value = "uv")
		public void setUv(List<Number> list) {
			uv = new Vector4f(
				list.get(0).floatValue(),
				list.get(1).floatValue(),
				list.get(2).floatValue(),
				list.get(3).floatValue()
			);
			was_uv_defined = true;
		}
		
		@JsonSetter(value = "cullface")
		public void setCullface(String face) {
			cullface = FaceType.get(face);
		}
		
		@JsonSetter(value = "tintindex")
		public void setTintIndex(Number value) {
			tintindex = value.intValue();
		}
		
		@JsonSetter(value = "rotation")
		public void setRotation(Number value) {
			rotation = value.intValue();
		}

		public JsonModelFace build(FaceType face, JsonModelElement element, JsonModelObject model) {
			JsonModelFace next = new JsonModelFace();
			next.cullface = cullface;
			next.tintindex = tintindex;
			next.rotation = rotation;
			if(texture.startsWith("#")) {
				next.texture = model.textures.get(texture.substring(1));
			} else {
				next.texture = texture;
			}
			
			if(!was_uv_defined) {
				uv = Maths.generateUv(face, element.from, element.to);
			}
			
			Vector4f next_uv = new Vector4f();
			next.uv = uv.get(next_uv);
			next.built_uv = rotateUvTest(uv, rotation);
			
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
	
	static class JsonModelElement {
		public Vector3f from = new Vector3f(0, 0, 0);
		public Vector3f to = new Vector3f(16, 16, 16);
		
		@JsonProperty(required = false)
		// This is true by default
		public boolean shade = true;
		
		@JsonProperty(required = false)
		private String __comment;
		
		@JsonProperty(required = false)
		public JsonRotation rotation;
		
		public Map<FaceType, JsonModelFace> faces = Map.of();
		
		
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
				FaceType face = FaceType.get(key);
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

		// This field is true by default
		@JsonProperty(required = false)
		private Boolean ambientocclusion = true; 
		
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
	
	private static float[] rotateUvArray(FaceType face_type, ModelFace face) {
		// 0, 1, 2, 0, 2, 3
		float[] va = face.vertex;
		Vector3f p0 = new Vector3f(va[0], va[1], va[2]).mul(16);
		Vector3f p1 = new Vector3f(va[3], va[4], va[5]).mul(16);
		Vector3f p2 = new Vector3f(va[6], va[7], va[8]).mul(16);
		Vector3f p3 = new Vector3f(va[15], va[16], va[17]).mul(16);
		
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
					p0.x, 16 - p0.z, // 0
					p1.x, 16 - p1.z, // 1
					p2.x, 16 - p2.z, // 2
					
					p0.x, 16 - p0.z, // 0
					p2.x, 16 - p2.z, // 2
					p3.x, 16 - p3.z, // 3
				};
				break;
			}
			case Direction.FACE_FRONT: {
				next_uv = new float[] {
					16 - p0.x, 16 - p0.y, // 0
					16 - p1.x, 16 - p1.y, // 1
					16 - p2.x, 16 - p2.y, // 2
					
					16 - p0.x, 16 - p0.y, // 0
					16 - p2.x, 16 - p2.y, // 2
					16 - p3.x, 16 - p3.y, // 3
				};
				break;
			}
			case Direction.FACE_BACK: {
				next_uv = new float[] {
					p0.x, 16 - p0.y, // 0
					p1.x, 16 - p1.y, // 1
					p2.x, 16 - p2.y, // 2
					
					p0.x, 16 - p0.y, // 0
					p2.x, 16 - p2.y, // 2
					p3.x, 16 - p3.y, // 3
				};
				break;
			}
			case Direction.FACE_RIGHT: {
				next_uv = new float[] {
					16 - p0.z, 16 - p0.y, // 0
					16 - p1.z, 16 - p1.y, // 1
					16 - p2.z, 16 - p2.y, // 2
					
					16 - p0.z, 16 - p0.y, // 0
					16 - p2.z, 16 - p2.y, // 2
					16 - p3.z, 16 - p3.y, // 3
				};
				break;
			}
			case Direction.FACE_LEFT: {
				next_uv = new float[] {
					p0.z, 16 - p0.y, // 0
					p1.z, 16 - p1.y, // 1
					p2.z, 16 - p2.y, // 2
					
					p0.z, 16 - p0.y, // 0
					p2.z, 16 - p2.y, // 2
					p3.z, 16 - p3.y, // 3
				};
				break;
			}
		}
		
		ProjectEdit.getInstance().getTextureManager().getBlockAtlas().transformModelUv(face.textureId, next_uv);
//		LwjglRender.atlas.transformModelUv(face.textureId, next_uv);
		return next_uv;
	}
	
	private static List<ModelElement> bakeDelegate(ModelDelegate model) {
		List<ModelElement> list = new ArrayList<>();
		
		Matrix4f mat = model.getTranslationMatrix().get(new Matrix4f());
		boolean uvlock = model.isUvLocked();
		
		for(ModelElement model_element : model.base.elements) {
			ModelElement element = new ModelElement();
			element.faces = new HashMap<>();
			
			for(Map.Entry<FaceType, ModelFace> entry : model_element.faces.entrySet()) {
				FaceType model_face_type = entry.getKey();
				ModelFace model_face = entry.getValue();
				
				ModelFace face = new ModelFace();
				
				FaceType face_type = model_face_type.rotate(mat);
				if(model_face.cullface != null) {
					face.cullface = model_face.cullface.rotate(mat);
				}
				
				face.textureId = model_face.textureId;
				face.vertex = transformCopy(model_face.vertex, mat);
				if(uvlock) {
					face.uv = rotateUvArray(face_type, face);
				} else {
					face.uv = model_face.uv.clone();
				}
				
				element.faces.put(face_type, face);
			}
			
			list.add(element);
		}
		
		return list;
	}
	
	public static class FastModel {
		private static final Matrix4f def_tm = new Matrix4f();
		
		// TODO: Cache these models and build them into the vertecies
		public static class ModelDelegate extends ModelObject {
			protected final ModelObject base;
			protected final List<ModelElement> baked;
			protected final Matrix4f matrix;
			protected final boolean uvlocked;
//			protected final int rot_x;
//			protected final int rot_y;
			
			public ModelDelegate(Matrix4f matrix, boolean uvlocked, ModelObject base) {
				this.matrix = matrix;
				this.uvlocked = uvlocked;
				this.base = base;
//				this.rot_x = x;
//				this.rot_y = y;
				this.baked = bakeDelegate(this);
			}
			
			@Override
			public List<ModelElement> getElements() {
				return baked;
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
			private List<ModelElement> elements;
			
			public List<ModelElement> getElements() {
				return elements;
			}
			
			public boolean isUvLocked() {
				return false;
			}
			
			public Matrix4f getTranslationMatrix() {
				return def_tm;
			}
		}
		
		public static class ModelElement {
			public Map<FaceType, ModelFace> faces;
		}
		
		public static class ModelFace {
//			private int rotation;
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
		}
		
		private static ModelFace createModelFace(FaceType type, JsonModelElement element, JsonModelFace json) {
			ModelFace face = new ModelFace();
			
			TextureAtlas atlas = ProjectEdit.getInstance().getTextureManager().getBlockAtlas();
			
			int id = atlas.getImageId(json.texture);
			if(id < 0) {
				BufferedImage image = resource.readBufferedImage(json.texture);
				id = atlas.addTexture(json.texture, image);
			}
			
			face.textureId = id;
			face.cullface = json.cullface;
			face.vertex = Maths.getModelVertexes(type, element.from, element.to);
			face.uv = json.built_uv.clone();
			atlas.transformModelUv(id, face.uv);
			
			return face;
		}
	}
	
	public static VersionResourceReader resource;
	private static Map<String, FastModel.ModelObject> cache_models = new HashMap<>();
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
			System.exit(0);
		}
		
		return null;
	}
	
	public static void unloadCache() {
		cache_models.clear();
	}
}
