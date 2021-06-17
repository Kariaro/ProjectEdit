package com.hardcoded.mc.constants;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.hardcoded.utils.FastModelJsonLoader.Axis;

public enum Direction {
	EAST(Direction.FACE_RIGHT, Axis.x, new Vector3f(1, 0, 0)),
	WEST(Direction.FACE_LEFT, Axis.x, new Vector3f(-1, 0, 0)),
	UP(Direction.FACE_UP, Axis.y, new Vector3f(0, 1, 0)),
	DOWN(Direction.FACE_DOWN, Axis.y, new Vector3f(0, -1, 0)),
	SOUTH(Direction.FACE_BACK, Axis.z, new Vector3f(0, 0, 1)),
	NORTH(Direction.FACE_FRONT, Axis.z, new Vector3f(0, 0, -1)),
	;
	
	private static final Map<String, Direction> FACE_TO_DIRECTION = new HashMap<>();
	static {
		FACE_TO_DIRECTION.put("front", NORTH);
		FACE_TO_DIRECTION.put("north", NORTH);
		
		FACE_TO_DIRECTION.put("back", SOUTH);
		FACE_TO_DIRECTION.put("south", SOUTH);
		
		FACE_TO_DIRECTION.put("bottom", DOWN);
		FACE_TO_DIRECTION.put("down", DOWN);
		
		FACE_TO_DIRECTION.put("right", EAST);
		FACE_TO_DIRECTION.put("east", EAST);
		
		FACE_TO_DIRECTION.put("left", WEST);
		FACE_TO_DIRECTION.put("west", WEST);
		
		FACE_TO_DIRECTION.put("top", UP);
		FACE_TO_DIRECTION.put("up", UP);
	}
	
	public static final int FACE_UP = 1;
	public static final int FACE_DOWN = 2;
	public static final int FACE_LEFT = 4;
	public static final int FACE_RIGHT = 8;
	public static final int FACE_FRONT = 16;
	public static final int FACE_BACK = 32;
	
	private final int flags;
	private final Vector3f normal;
	private final Axis axis;
	private Direction(int flags, Axis axis, Vector3f normal) {
		this.flags = flags;
		this.normal = normal;
		this.axis = axis;
	}
	
	public Vector3f getNormal() {
		return normal;
	}
	
	public Axis getAxis() {
		return axis;
	}
	
	public int getFlags() {
		return flags;
	}

	public Direction rotate(Matrix4f mat) {
		switch(this) {
			case WEST: return Direction.getFromNormal(-mat.m00(), -mat.m01(), -mat.m02());
			case EAST: return Direction.getFromNormal(mat.m00(), mat.m01(), mat.m02());
			case DOWN: return Direction.getFromNormal(-mat.m10(), -mat.m11(), -mat.m12());
			case UP: return Direction.getFromNormal(mat.m10(), mat.m11(), mat.m12());
			case SOUTH: return Direction.getFromNormal(mat.m20(), mat.m21(), mat.m22());
			case NORTH: return Direction.getFromNormal(-mat.m20(), -mat.m21(), -mat.m22());
		}
		
		throw new UnsupportedOperationException("Invalid direction");
	}
	
	public static Direction getFromNormal(Vector3f normal) {
		normal = normal.normalize();
		if(normal.x >  0.7) return Direction.EAST;
		if(normal.x < -0.7) return Direction.WEST;
		if(normal.y >  0.7) return Direction.UP;
		if(normal.y < -0.7) return Direction.DOWN;
		if(normal.z >  0.7) return Direction.SOUTH;
		if(normal.z < -0.7) return Direction.NORTH;
		throw new UnsupportedOperationException("Invalid direction");
	}
	
	public static Direction getFromNormal(float x, float y, float z) {
		float div = 1.0f / (float)Math.sqrt(x*x + y*y + z*z);
		x *= div; y *= div; z *= div;
		if(x >  0.7) return Direction.EAST;
		if(x < -0.7) return Direction.WEST;
		if(y >  0.7) return Direction.UP;
		if(y < -0.7) return Direction.DOWN;
		if(z >  0.7) return Direction.SOUTH;
		if(z < -0.7) return Direction.NORTH;
		throw new UnsupportedOperationException("Invalid direction");
	}
	
	public static Direction get(String name) {
		return FACE_TO_DIRECTION.get(name.toLowerCase());
	}
}
