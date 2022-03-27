package com.hardcoded.util.math.box;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;

import com.hardcoded.mc.constants.Direction;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelElement;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelFace;

public class BoxShape {
	private final Map<Direction, BoxFace> facesMap;
	public Vector3f from;
	public Vector3f to;
	
	public BoxShape(Vector3f from, Vector3f to) {
		this.from = from.get(new Vector3f());
		this.to = to.get(new Vector3f());
		this.facesMap = null;
	}
	
	/*
	public BoxShape(Vector3f from, Vector3f to, ModelElement element) {
		this.from = from.get(new Vector3f());
		this.to = to.get(new Vector3f());
		
		Map<Direction, BoxFace> map = new HashMap<>();
		
		for(Map.Entry<Direction, ModelFace> entry : element.faces.entrySet()) {
			map.put(entry.getKey(), entry.getValue().box_face);
		}
		
		this.facesMap = Map.copyOf(map);
	}
	
	public BoxShape(Vector3f from, Vector3f to, BoxShape copy) {
		this.from = from.get(new Vector3f());
		this.to = to.get(new Vector3f());
		
		if(copy.facesMap != null) {
			this.facesMap = Map.copyOf(copy.facesMap);
		} else {
			this.facesMap = null;
		}
	}
	*/

	public BoxFace getFace(Direction direction) {
//		if(facesMap != null) {
//			BoxFace face = this.facesMap.get(direction);
//			if(face != null) return face;
//		}
		
		switch(direction) {
			case SOUTH: // back
			case NORTH: // front
				return new BoxFace(from.x, from.y, to.x, to.y, direction.getAxisDirection() < 0 ? from.z:to.z, direction);
			case EAST: // right
			case WEST: // left
				return new BoxFace(from.z, from.y, to.z, to.y, direction.getAxisDirection() < 0 ? from.x:to.x, direction);
			case UP: // up
			case DOWN: // down
				return new BoxFace(from.x, from.z, to.x, to.z, direction.getAxisDirection() < 0 ? from.y:to.y, direction);
		}
		
		throw new UnsupportedOperationException();
	}
}