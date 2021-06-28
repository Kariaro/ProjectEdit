package com.hardcoded.util.math.box;

import org.joml.Vector3f;

import com.hardcoded.mc.constants.Direction;

public class BoxShape {
	public Vector3f from;
	public Vector3f to;
	
	public BoxShape(Vector3f from, Vector3f to) {
		this.from = from.get(new Vector3f());
		this.to = to.get(new Vector3f());
	}
	
	public BoxFace getFace(Direction direction) {
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