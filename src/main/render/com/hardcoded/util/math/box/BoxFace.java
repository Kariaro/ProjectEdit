package com.hardcoded.util.math.box;

import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;
import com.hardcoded.mc.constants.Direction;

public class BoxFace {
	public final Direction direction;
	public final float x0;
	public final float y0;
	public final float x1;
	public final float y1;
	public final Uv uv;
	public final float depth;
	
	public BoxFace(float x0, float y0, float x1, float y1, float depth, Direction direction) {
		this(x0, y0, x1, y1, null, depth, direction);
	}
	
	public BoxFace(float x0, float y0, float x1, float y1, AtlasUv uv, float depth, Direction direction) {
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
		
		if(uv != null) {
			this.uv = new Uv(uv);
		} else {
			this.uv = null;
		}
	}
	
	public boolean contains(float x, float y) {
		return x <= x1 && x > x0 && y <= y1 && y > y0;
	}
	
	public float getArea() {
		return (x1 - x0) * (y1 - y0);
	}
	
	public boolean hasPixel(int x, int y) {
		if(true) return true;
		
		if(uv == null) {
			return true;
		}
		
		TextureAtlas atlas = uv.atlas;
		int W = atlas.getWidth();
		int H = atlas.getHeight();
		
		float xp = (x - x0) / (BitArrayShape.SIDE + 0.0f);
		float yp = (y - y0) / (BitArrayShape.SIDE + 0.0f);
		
		float tw = uv.x1 - uv.x0;
		float th = uv.y1 - uv.y0;
		
		float sx = uv.x0 + (xp * tw);
		float sy = uv.y0 + (yp * th);
		
		int ax = (int)(sx * W);
		int ay = (int)(sy * H);
		
		return !atlas.isTransparent(ax, ay, x, y);
	}
	
	private class Uv {
		public final TextureAtlas atlas;
		public final float x0;
		public final float y0;
		public final float x1;
		public final float y1;
		
		public Uv(AtlasUv uv) {
			this.atlas = uv.getParent();
			this.x0 = uv.x0;
			this.y0 = uv.y0;
			this.x1 = uv.x1;
			this.y1 = uv.y1;
		}
	}
}
