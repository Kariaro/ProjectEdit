package com.hardcoded.render.utils;

import org.joml.Vector3f;

import com.hardcoded.utils.FastModelJsonLoader.FaceType;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelFace;
import com.hardcoded.utils.FastModelJsonLoader.FastModel.ModelObject;

public class CullFaceCache {
	/**
	 * Returns true if this face is ocluded by the object
	 */
	private static boolean isCulled(ModelFace a, ModelObject b, FaceType type) {
		Vector3f normal = type.getNormal();
		
		return false;
	}
}
