package com.hardcoded.utils;

import java.util.List;

import com.hardcoded.mc.constants.Direction;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.render.utils.MeshBuilder;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelObject;

public class FastModelRenderer {
	public static boolean hasFace(FaceType face, int faces) {
		return (face.getFlags() & faces) != 0;
	}

	private static final float[] LIT_100 = { 1.0f, 1.0f, 1.0f };
	private static final float[] LIT_90 = { 0.9f, 0.9f, 0.9f };
	private static final float[] LIT_70 = { 0.7f, 0.7f, 0.7f };
	private static final float[] LIT_50 = { 0.5f, 0.5f, 0.5f };
	
	public static float[] getFaceColor(FaceType face) {
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
	
	public static float[] getDebugFaceColor(FaceType face) {
		switch(face) {
			case up:
				return new float[] { 0.3f, 1.0f, 0.3f };
			case down:
				return new float[] { 0.1f, 0.5f, 0.1f };
			case east:
				return new float[] { 1.0f, 0.3f, 0.3f };
			case west:
				return new float[] { 0.5f, 0.1f, 0.1f };
			case north:
				return new float[] { 0.3f, 0.3f, 1.0f };
			case south:
				return new float[] { 0.1f, 0.1f, 0.5f };
			default:
				return new float[] { 0.0f, 0.0f, 0.0f };
		}
	}
	
	public static void renderModel(IBlockData bs, float x, float y, float z, MeshBuilder builder, int faces) {
		if(bs instanceof BlockData) {
			renderModelFast((BlockData)bs, x, y, z, builder, faces);
		}
	}
	
	public static void renderModelFast(BlockData bs, float x, float y, float z, MeshBuilder builder, int faces) {
		List<ModelObject> model_objects = bs.model_objects;
		for(int i = 0, il = model_objects.size(); i < il; i++) {
			ModelObject model = model_objects.get(i);
			
			for(ModelElement element : model.getElements()) {
				for(FaceType type : element.faces.keySet()) {
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
}
