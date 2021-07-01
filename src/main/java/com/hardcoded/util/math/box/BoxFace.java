package com.hardcoded.util.math.box;

import com.hardcoded.mc.constants.Direction;

class BoxFace {
	public final Direction direction;
	public final float x0;
	public final float y0;
	public final float x1;
	public final float y1;
	public final float depth;
	
	public BoxFace(float x0, float y0, float x1, float y1, float depth, Direction direction) {
		this.direction = direction;
		float tmp;
		if(x0 > x1) {
			tmp = x0;
			x0 = x1;
			x1 = tmp;
		}
		
		if(y0 > y1) {
			tmp = y0;
			y0 = y1;
			y1 = tmp;
		}
		
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		this.depth = depth;
	}
	
	public boolean contains(float x, float y) {
		return x <= x1 && x > x0 && y <= y1 && y > y0;
	}
	
	public float getArea() {
		return (x1 - x0) * (y1 - y0);
	}
}
