package com.hardcoded.util.math.box;

import java.util.*;
import java.util.stream.Collectors;

import com.hardcoded.mc.constants.Direction;
import com.hardcoded.render.generator.FastModelJsonLoader.FastModel.ModelObject;
import com.hardcoded.util.MathUtils;

public class BlockShape {
	public static final BlockShape FULL_BLOCK = new BlockShape() {
		@Override
		public boolean isFullCube() {
			return true;
		}
		
		@Override
		public boolean isSideBlockedBy(BlockShape adjacent, Direction direction) {
			return adjacent.isFullCube();
		}
	};
	
	protected static final double TOLERANCE = 1 / 1024.0f;
	
	protected final Map<Direction, Set<BoxFace>> faces;
	
	protected BlockShape(List<ModelObject> list) {
		List<BoxShape> shapes = list.stream().flatMap(i -> i.getShapes().stream()).collect(Collectors.toList());
		
		this.faces = new HashMap<>();
		for(Direction direction : Direction.getFaces()) {
			// Convert from (-1, 1) to (0, 16)
			int depthValue = direction.getAxisDirection() * 8 + 8;
			
			Set<BoxFace> faces = null;
			for(BoxShape shape : shapes) {
				BoxFace face = shape.getFace(direction);
				
				// Smaller than a single pixel
				// Because BitArrayShape allows culling pixels to be 1/32 of a block
				if(face.getArea() < 0.25) continue;
				
				// If the face does not touch the edge of the block
				if(!MathUtils.fuzzyEquals(face.depth, depthValue, TOLERANCE)) continue;
				
				// Add the face
				if(faces == null) faces = new HashSet<>();
				faces.add(face);
			}
			
			if(faces == null) faces = Set.of();
			this.faces.put(direction, faces);
		}
	}
	
	protected BlockShape() {
		this.faces = null;
	}
	
	public boolean isSideBlockedBy(BlockShape adjacent, Direction direction) {
		return false;
	}
	
	public boolean isFullCube() {
		return false;
	}
	
	public boolean isEmptyCube() {
		return false;
	}
}
