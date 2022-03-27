package com.hardcoded.render.generator;

import java.util.*;

import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.mesh.MeshBuffer;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.constants.Direction;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.Biome;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.IBlockState;
import com.hardcoded.render.BiomeBlend;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelFace;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelObject;
import com.hardcoded.util.math.box.BitArrayShape;
import com.hardcoded.util.math.box.BlockShape;

public class FastModelRenderer {
	private static final Map<IBlockData, List<FastModel.ModelObject>> model_cache = new HashMap<>();
	private static final Map<IBlockData, BlockShape> shape_cache = new HashMap<>();
	private static final Set<IBlockData> has_elements = new HashSet<>();
	private static final Set<IBlockData> has_translucency = new HashSet<>();
	
	private static final float[] LIT_100 = { 1.0f, 1.0f, 1.0f, 1.0f };
	private static final float[] LIT_90 = { 0.9f, 0.9f, 0.9f, 1.0f };
	private static final float[] LIT_70 = { 0.7f, 0.7f, 0.7f, 1.0f };
	private static final float[] LIT_50 = { 0.5f, 0.5f, 0.5f, 1.0f };
	
	public static float[] getFaceColor(Direction face) {
		switch(face.getFlags()) {
			case Direction.FACE_BACK:
			case Direction.FACE_FRONT:
				return LIT_90;
			case Direction.FACE_LEFT:
			case Direction.FACE_RIGHT:
				return LIT_70;
			case Direction.FACE_UP:
				return LIT_100;
			default:
				return LIT_50;
		}
	}
	
	public static float[] getDebugFaceColor(Direction face) {
		switch(face) {
			case UP:
				return new float[] { 0.3f, 1.0f, 0.3f, 1.0f };
			case DOWN:
				return new float[] { 0.1f, 0.5f, 0.1f, 1.0f };
			case EAST:
				return new float[] { 1.0f, 0.3f, 0.3f, 1.0f };
			case WEST:
				return new float[] { 0.5f, 0.1f, 0.1f, 1.0f };
			case NORTH:
				return new float[] { 0.3f, 0.3f, 1.0f, 1.0f };
			case SOUTH:
				return new float[] { 0.1f, 0.1f, 0.5f, 1.0f };
			default:
				return new float[] { 0.0f, 0.0f, 0.0f, 1.0f };
		}
	}
	
	public static void unloadModelCache() {
		model_cache.clear();
		has_elements.clear();
		has_translucency.clear();
		shape_cache.clear();
	}
	
	protected static void addModel(IBlockData block, ModelObject model) {
		List<ModelObject> models = model_cache.get(block);
		if(models == null) {
			models = new ArrayList<>();
			model_cache.put(block, models);
		}
		
		models.add(model);
		
		if(model.hasElements()) {
			has_elements.add(block);
		}
		
		TextureAtlas atlas = ProjectEdit.getInstance().getTextureManager().getBlockAtlas().getMain();
		
		for(ModelObject object : models) {
			for(ModelElement element : object.getElements()) {
				for(ModelFace face : element.faces.values()) {
					if(atlas.isTranclucent(face.uv)) {
						has_translucency.add(block);
						return;
					}
				}
			}
		}
		
		BlockShape shape = shape_cache.get(block);
		if(shape instanceof BitArrayShape) {
			shape = new BitArrayShape(models, (BitArrayShape)shape);
		} else {
			shape = new BitArrayShape(models);
		}
		
		shape_cache.put(block, shape);
	}
	
	public static boolean hasLoadedModel(IBlockData block) {
		return has_elements.contains(block);
	}
	
	public static boolean hasTranslucency(IBlockData block) {
		return has_translucency.contains(block);
	}
	
	public static BlockShape getBlockShape(IBlockData block) {
		return shape_cache.getOrDefault(block, BlockShape.FULL_BLOCK);
	}
	
	public static List<ModelObject> getModelsForBlock(IBlockData block) {
		List<ModelObject> model_objects = model_cache.get(block);
		if(model_objects == null) {
			model_objects = List.of(FastModelJsonLoader.getMissingBlockModel());
			
			// XXX: This can be called from multiple threads without causing any errors.
			// This will at some point be replaced multiple times because of threading but will cause no errors.
			model_cache.put(block, model_objects);
		}
		
		return model_objects;
	}
	
	public static void renderModel(IBlockData bs, float x, float y, float z, MeshBuffer builder, int faces) {
		List<ModelObject> model_objects = getModelsForBlock(bs);
		
		for(int i = 0, il = model_objects.size(); i < il; i++) {
			ModelObject model = model_objects.get(i);
			
			for(ModelElement element : model.getElements()) {
				for(Direction type : element.faces.keySet()) {
					ModelFace face = element.faces.get(type);
					
					if(face.cullface == null || (face.cullface.getFlags() & faces) != 0) {
						float[] uv = face.uv;
						float[] vertex = face.vertex;
						builder.uv(uv);
						
						float[] color = getFaceColor(type);
						for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
							float ax = vertex[vi] + x;
							float ay = vertex[vi + 1] + y;
							float az = vertex[vi + 2] + z;
							builder.pos(ax, ay, az);
							builder.color(color);
						}
					}
				}
			}
		}
	}
	
	public static void renderModelFastBiome(IBlockData bs, Biome biome, float x, float y, float z, MeshBuffer builder, int faces) {
		List<ModelObject> model_objects = getModelsForBlock(bs);
		
		boolean isRedstone = bs.getBlockId() == Blocks.get(Blocks.REDSTONE_WIRE).getBlockId();
		int level = 0;
		if(isRedstone) {
			Object power = bs.getStateList().getState(IBlockState.States._power);
			if(power != null) {
				try {
					level = Integer.parseInt(power.toString());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		for(int i = 0, il = model_objects.size(); i < il; i++) {
			ModelObject model = model_objects.get(i);
			
			for(ModelElement element : model.getElements()) {
				for(Direction type : element.faces.keySet()) {
					ModelFace face = element.faces.get(type);
					
					if(face.cullface == null || (face.cullface.getFlags() & faces) != 0) {
						float[] uv = face.uv;
						float[] vertex = face.vertex;
						builder.uv(uv);
						
						float[] color = getFaceColor(type);
//						color = getDebugFaceColor(type);
//						color[0] += (1 - color[0]) / 4.0f;
//						color[1] += (1 - color[1]) / 4.0f;
//						color[2] += (1 - color[2]) / 4.0f;
						
						if(face.tintIndex >= 0) {
							int rgb = BiomeBlend.get(biome.getTemperature(), biome.getDownfall());
							color = color.clone();
							
							if(isRedstone) {
								for(int vi = 0, len = color.length; vi < len; vi += 4) {
									color[vi    ] = 0.3f + (level / 15.0f) * 0.7f;
									color[vi + 1] = 0.0f;
									color[vi + 2] = 0.0f;
								}
							} else {
								for(int vi = 0, len = color.length; vi < len; vi += 4) {
									color[vi    ] = ((rgb >> 16) & 0xff) / 255.0f;
									color[vi + 1] = ((rgb >>  8) & 0xff) / 255.0f;
									color[vi + 2] = ((rgb      ) & 0xff) / 255.0f;
								}
							}
						}
						
						for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
							float ax = vertex[vi] + x;
							float ay = vertex[vi + 1] + y;
							float az = vertex[vi + 2] + z;
							builder.pos(ax, ay, az);
							builder.color(color);
						}
					}
				}
			}
			
//			for(BoxShape shape : model.getShapes()) {
//				int rgb = shape.hashCode();
//				
//				for(Direction type : Direction.getFaces()) {
//					float[] vertex = Maths.getModelVertexes(type, shape.from, shape.to);
//					
//					boolean culled = (type.getFlags() & faces) == 0;
////					if((type.getFlags() & faces) == 0) continue;
//					
//					float c_mul = getFaceColor(type)[0];
//					for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
//						float ax = vertex[vi] / 16.0f + x;
//						float ay = vertex[vi + 1] / 16.0f + y;
//						float az = vertex[vi + 2] / 16.0f + z;
//						builder.pos(ax, ay, az);
//						
//						float r = ((rgb >> 16) & 0xff) / 255.0f;
//						float g = ((rgb >>  8) & 0xff) / 255.0f;
//						float b = ((rgb      ) & 0xff) / 255.0f;
//						if(culled) {
//							builder.color(1, 1, 1);
//						} else {
//							builder.color(r * c_mul, g * c_mul, b * c_mul);
//						}
//						builder.uv(0, 0);
//					}
//				}
//			}
		}
	}
	
	public static void renderModelObject(ModelObject model, float x, float y, float z, MeshBuffer builder, int faces) {
		for(ModelElement element : model.getElements()) {
			for(Direction type : element.faces.keySet()) {
				ModelFace face = element.faces.get(type);
				
				if(face.cullface == null || (face.cullface.getFlags() & faces) != 0) {
					float[] uv = face.uv;
					float[] vertex = face.vertex;
					builder.uv(uv);
					
					float[] color = getFaceColor(type);
					for(int vi = 0, len = vertex.length; vi < len; vi += 3) {
						float ax = vertex[vi] + x;
						float ay = vertex[vi + 1] + y;
						float az = vertex[vi + 2] + z;
						builder.pos(ax, ay, az);
						builder.color(color);
					}
				}
			}
		}
	}
}
