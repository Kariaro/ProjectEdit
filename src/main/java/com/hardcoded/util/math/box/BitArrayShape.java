package com.hardcoded.util.math.box;

import java.util.List;
import java.util.Set;

import com.hardcoded.mc.constants.Direction;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelObject;

/**
 * This class uses a bit array to determine if a face
 * on a block is hidden by another blocks face.
 * 
 * @author HardCoded
 */
public class BitArrayShape extends BlockShape {
	private static final int SIDE = 32;
	private static final int BITS_ARRAY = SIDE >> 1;
	private static final float BLOCK_TO_BITS = SIDE / (float)16;
	
	// Each face has its own array of bits
	private final BooleanFace[] booleanFaces = new BooleanFace[6];
	private final boolean isFullCube;
	private final boolean isEmptyCube;
	
	public BitArrayShape(List<ModelObject> list) {
		super(list);
		
		for(Direction direction : Direction.getFaces()) {
			booleanFaces[direction.getIndex()] = new BooleanFace(faces.get(direction), direction);
		}
		
		boolean isEmptyCube = true;
		boolean isFullCube = true;
		for(int i = 0; i < 6; i++) {
			BooleanFace face = booleanFaces[i];
			
			if(!face.isEmpty) isEmptyCube = false;
			if(!face.isFull) isFullCube = false;
		}
		
		this.isFullCube = isFullCube;
		this.isEmptyCube = isEmptyCube;
	}
	
	public BitArrayShape(List<ModelObject> list, BitArrayShape parent) {
		super(list);
		
		for(Direction direction : Direction.getFaces()) {
			booleanFaces[direction.getIndex()] = new BooleanFace(parent.booleanFaces[direction.getIndex()], faces.get(direction), direction);
		}
		
		boolean isEmptyCube = true;
		boolean isFullCube = true;
		for(int i = 0; i < 6; i++) {
			BooleanFace face = booleanFaces[i];
			
			if(!face.isEmpty) isEmptyCube = false;
			if(!face.isFull) isFullCube = false;
		}
		
		this.isFullCube = isFullCube;
		this.isEmptyCube = isEmptyCube;
	}
	
	private class BooleanFace {
		private final long[] data = new long[BITS_ARRAY];
		private final boolean isFull;
		private final boolean isEmpty;
		
		public BooleanFace(Set<BoxFace> list, Direction direction) {
			for(BoxFace face : list) {
				addFace(face);
			}
			
			boolean isEmpty = true;
			boolean isFull = true;
			for(int i = 0; i < BITS_ARRAY; i++) {
				long value = data[i];
				
				if(value != 0L) isEmpty = false;
				if(value != -1L) isFull = false;
			}
			
			this.isEmpty = isEmpty;
			this.isFull = isFull;
		}
		
		public BooleanFace(BooleanFace parent, Set<BoxFace> list, Direction direction) {
			for(int i = 0; i < BITS_ARRAY; i++) {
				data[i] = parent.data[i];
			}
			
			for(BoxFace face : list) {
				addFace(face);
			}
			
			boolean isEmpty = true;
			boolean isFull = true;
			for(int i = 0; i < BITS_ARRAY; i++) {
				long value = data[i];
				
				if(value != 0L) isEmpty = false;
				if(value != -1L) isFull = false;
			}
			
			this.isEmpty = isEmpty;
			this.isFull = isFull;
		}
		
		void addFace(BoxFace face) {
			int xs = (int)(face.x0 * BLOCK_TO_BITS);
			int xe = (int)(face.x1 * BLOCK_TO_BITS);
			int ys = (int)(face.y0 * BLOCK_TO_BITS);
			int ye = (int)(face.y1 * BLOCK_TO_BITS);
			
			if(xs < 0) xs = 0;
			if(xe > SIDE) xe = SIDE;
			if(ys < 0) ys = 0;
			if(ye > SIDE) ye = SIDE;
			
			for(int y = ys; y < ye; y++) {
				for(int x = xs; x < xe; x++) {
					set(x, y, true);
				}
			}
		}
		
		void set(int x, int y, boolean enable) {
			final int idx = (x >> 5) + (y >> 1);
			final long bits = (x + ((y & 1L) << 5L)) & 63L;
			long read = data[idx];
			if(enable) {
				data[idx] = read | (1L << bits);
			} else {
				data[idx] = read & ~(1L << bits);
			}
		}
		
		public boolean isBlockedBy(BooleanFace adjacent) {
			// If this face is empty it's always blocked
			// If adjacent is full then it always blocks
			if(adjacent.isFull) return true;
			if(isEmpty) return false;
			
			// If this face is full and adjacent is not full it's not blocked
			// If adjacent is empty then it's never blocked
			if((isFull && !adjacent.isFull) || (adjacent.isEmpty)) return false;
			
			// If this face is blocked by ajacent then it means that
			// For all values in this array
			//   (data[i] & ~(adjacent.data[i])) == 0
			
			// This is because if we take all spaces that is not blocked
			// and it's not zero it means that not everything is blocked.
			for(int i = 0; i < BITS_ARRAY; i++) {
				if((data[i] & ~(adjacent.data[i])) != 0) return false;
			}
			
			return true;
		}
	}
	
	@Override
	public boolean isSideBlockedBy(BlockShape shape, Direction direction) {
		if(!(shape instanceof BitArrayShape)) return shape.isFullCube();
		
		BooleanFace adjacent = ((BitArrayShape)shape).booleanFaces[direction.getOpposite().getIndex()];
		BooleanFace origin = booleanFaces[direction.getIndex()];
		return origin.isBlockedBy(adjacent);
	}
	
	@Override
	public boolean isFullCube() {
		return isFullCube;
	}
	
	@Override
	public boolean isEmptyCube() {
		return isEmptyCube;
	}
}
