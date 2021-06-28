package com.hardcoded.mc.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public enum Direction {
	/** right */
	EAST(0, Direction.FACE_RIGHT, Axis.x, 1, new Vector3f(1, 0, 0)),
	/** left */
	WEST(1, Direction.FACE_LEFT, Axis.x, -1, new Vector3f(-1, 0, 0)),
	/** top */
	UP(2, Direction.FACE_UP, Axis.y, 1, new Vector3f(0, 1, 0)),
	/** bottom */
	DOWN(3, Direction.FACE_DOWN, Axis.y, -1, new Vector3f(0, -1, 0)),
	/** back */
	SOUTH(4, Direction.FACE_BACK, Axis.z, 1, new Vector3f(0, 0, 1)),
	/** front */
	NORTH(5, Direction.FACE_FRONT, Axis.z, -1, new Vector3f(0, 0, -1));
	
	private static final Map<String, Direction> FACE_TO_DIRECTION = new HashMap<>();
	private static final Map<Direction, Direction> OPPOSITE_DIRECTION = new HashMap<>();
	private static final Set<Direction> FACES;
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
		
		OPPOSITE_DIRECTION.put(NORTH, SOUTH);
		OPPOSITE_DIRECTION.put(SOUTH, NORTH);
		OPPOSITE_DIRECTION.put(EAST, WEST);
		OPPOSITE_DIRECTION.put(WEST, EAST);
		OPPOSITE_DIRECTION.put(UP, DOWN);
		OPPOSITE_DIRECTION.put(DOWN, UP);
		
		FACES = Set.of(NORTH, SOUTH, EAST, WEST, UP, DOWN);
	}
	
	public static final int FACE_UP = 1;
	public static final int FACE_DOWN = 2;
	public static final int FACE_LEFT = 4;
	public static final int FACE_RIGHT = 8;
	public static final int FACE_FRONT = 16;
	public static final int FACE_BACK = 32;
	
	private final int flags;
	private final int index;
	private final Vector3f normal;
	private final int axisDirection;
	private final Axis axis;
	private Direction(int index, int flags, Axis axis, int axisDirection, Vector3f normal) {
		this.index = index;
		this.flags = flags;
		this.normal = normal;
		this.axisDirection = axisDirection;
		this.axis = axis;
	}
	
	public Vector3f getNormal() {
		// Return a unique value every time
		return normal.get(new Vector3f());
	}
	
	/**
	 * Returns the index of this direction.
	 * Will always return:
	 * <pre>
	 *   EAST:  0
	 *   WEST:  1
	 *   UP:    2
	 *   DOWN:  3
	 *   SOUTH: 4
	 *   NORTH: 5
	 * </pre>
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Returns {@code -1} or {@code 1}
	 */
	public int getAxisDirection() {
		return axisDirection;
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
	
	public Direction getOpposite() {
		return OPPOSITE_DIRECTION.get(this);
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
	
	public static Set<Direction> getFaces() {
		return FACES;
	}
}
