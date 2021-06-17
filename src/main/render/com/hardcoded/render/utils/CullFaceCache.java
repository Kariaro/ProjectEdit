package com.hardcoded.render.utils;

import java.util.HashMap;
import java.util.Map;

import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.utils.FastModelJsonLoader.FaceType;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelObject;

public class CullFaceCache {
	private static final Map<IBlockData, Map<IBlockData, Map<FaceType, Boolean>>> cull_map = new HashMap<>();
	private static final Map<ModelFace, Map<ModelFace, Boolean>> face_cull_map = new HashMap<>();
	
	private static boolean isCulled(ModelFace a, ModelObject b, FaceType type) {
		// If this face is north the other face must be south 
		FaceType coll = FaceType.getFromNormal(type.getNormal());
		
		float[] a_vert = a.vertex;
		for(ModelElement elm : b.getElements()) {
			ModelFace face = elm.faces.get(coll);
			if(face == null) continue;
			
			
		}
		return false;
	}
	
	private static boolean checkIfCulled(IBlockData a, IBlockData b, FaceType face) {
		BlockData A = (BlockData)a;
		BlockData B = (BlockData)b;
		
		return false;
	}
	
	/**
	 * Returns true if this face is ocluded by the object
	 */
	public static boolean isCulled(IBlockData a, IBlockData b, FaceType face) {
		if(a == null || b == null) return false;
		
		Map<IBlockData, Map<FaceType, Boolean>> map = cull_map.get(a);
		if(map == null) {
			map = new HashMap<>();
			cull_map.put(a, map);
		}
		
		Map<FaceType, Boolean> compare = map.get(b);
		if(compare == null) {
			compare = new HashMap<>();
			map.put(b, compare);
		}
		
		Boolean result = compare.get(face);
		if(result == null) {
			result = checkIfCulled(a, b, face);
			compare.put(face, result);
		}
		
		return result;
	}
}
