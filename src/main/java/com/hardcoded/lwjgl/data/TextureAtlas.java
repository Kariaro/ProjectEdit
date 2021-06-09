package com.hardcoded.lwjgl.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class TextureAtlas {
	private static final int WIDTH = 2048;
	private static final int HEIGHT = 2048;
	private static final int ALIGN = 16;
	private static final int ATLAS_MAP_WIDTH = WIDTH / ALIGN;
	private static final int ATLAS_MAP_HEIGHT = HEIGHT / ALIGN;
	private static final int PADDING = 1;
	
	private Texture atlas;
	private final BufferedImage image;
	private final AtlasUv[] atlas_map = new AtlasUv[ATLAS_MAP_WIDTH * ATLAS_MAP_HEIGHT];
	private final Map<String, Integer> atlas_path_to_id;
	
	public TextureAtlas() {
		this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		this.atlas_path_to_id = new HashMap<>();
	}
	
	public int addTexture(String path, BufferedImage bi) {
		if(atlas_path_to_id.containsKey(path)) {
			return atlas_path_to_id.get(path);
		}
		
		int aw = (bi.getWidth() + ALIGN - 1) / ALIGN;
		int ah = (bi.getHeight() + ALIGN - 1) / ALIGN;
		
		int id = findSpace(aw, ah);
		if(id < 0)
			throw new ArrayIndexOutOfBoundsException("Atlas map could not fit texture: bi[ width=" + bi.getWidth() + ", height=" + bi.getHeight() + "]; " + bi);
		
		Graphics2D g = image.createGraphics();
		int xp = id % ATLAS_MAP_WIDTH;
		int yp = id / ATLAS_MAP_WIDTH;
		
		applyPadding(g, bi, xp, yp);
		g.setBackground(new Color(0, true));
		g.clearRect(xp * ALIGN, yp * ALIGN, bi.getWidth(), bi.getHeight());
		g.drawImage(bi, xp * ALIGN, yp * ALIGN, null);
		g.dispose();
		
		AtlasUv uv = new AtlasUv(id,
			(xp * ALIGN) / (WIDTH + 0.0f),
			(yp * ALIGN) / (HEIGHT + 0.0f),
			(xp * ALIGN + bi.getWidth()) / (WIDTH + 0.0f),
			(yp * ALIGN + bi.getHeight()) / (HEIGHT + 0.0f)
		);
		int width = xp + aw;
		int height = yp + ah;
		for(int y = yp; y < height; y++) {
			for(int x = xp; x < width; x++) {
				atlas_map[x + y * ATLAS_MAP_WIDTH] = uv;
			}
		}
		
		atlas_path_to_id.put(path, id);
		return id;
	}
	
	private void applyPadding(Graphics2D g, BufferedImage bi, int xp, int yp) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		int x1 = xp * ALIGN;
		int y1 = yp * ALIGN;
		int x2 = x1 + w;
		int y2 = y1 + h;
		int p = ALIGN / 2;
		
		// . # .
		// #   #
		// . # .
		g.drawImage(bi, x1 - p, y1, x1, y2, 0, 0, 1, h, null);
		g.drawImage(bi, x2, y1, x2 + p, y2, w - 1, 0, w, h, null);
		g.drawImage(bi, x1, y1 - p, x2, y1, 0, 0, w, 1, null);
		g.drawImage(bi, x1, y2, x2, y2 + p, 0, h - 1, w, h, null);
		
		// # - #
		// |   |
		// # - #
		g.drawImage(bi, x1 - p, y1 - p, x1, y1, 0, 0, 1, 1, null);
		g.drawImage(bi, x2, y1 - p, x2 + p, y1, w - 1, 0, w, 1, null);
		g.drawImage(bi, x1 - p, y2, x1, y2 + p, 0, h - 1, 1, h, null);
		g.drawImage(bi, x2, y2, x2 + p, y2 + p, w - 1, h - 1, w, h, null);
	}

	public void compile() {
		if(atlas != null) {
			atlas.cleanup();
			atlas = null;
		}
		
		atlas = Texture.loadBufferedImageTexture(image, GL11.GL_NEAREST);
	}
	
	public AtlasUv getUv(int id) {
		return atlas_map[id];
	}
	
	private int findSpace(int w, int h) {
		int ys = PADDING;
		int xs = PADDING;
		int ye = ATLAS_MAP_HEIGHT - h - PADDING;
		int xe = ATLAS_MAP_WIDTH - w - PADDING;
		int pad = PADDING * 2;
		
		for(int y = ys; y < ye; y++) {
			for(int x = xs; x < xe; x++) {
				if(hasSpace(x - PADDING, y - PADDING, x + w + pad, y + h + pad)) {
					return (x) + (y * ATLAS_MAP_WIDTH);
				}
			}
		}
		
		return -1;
	}
	
	private boolean hasSpace(int x1, int y1, int x2, int y2) {
		for(int y = y1; y < y2; y++) {
			for(int x = x1; x < x2; x++) {
				if(atlas_map[x + y * ATLAS_MAP_HEIGHT] != null) return false;
			}
		}
		
		return true;
	}
	
	public Texture getTexture() {
		return atlas;
	}
	
	public void transformModelUv(int id, float[] uvs) {
		AtlasUv uv = getUv(id);
		uv.modify(uvs);
	}
	
	public void bind() {
		if(atlas != null) {
			atlas.bind();
		}
	}
	
	public void unbind() {
		if(atlas != null) {
			atlas.unbind();
		}
	}
	
	public class AtlasUv {
		public final int id;
		public float x1, y1;
		public float x2, y2;
		
		public AtlasUv(int id, float x1, float y1, float x2, float y2) {
			this.id = id;
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public void modify(float[] uvs) {
			final int len = uvs.length;
//			float xc = 0;
//			float yc = 0;
			
			// Transform from texture coordinates to this coordinates
			for(int i = 0; i < len; i += 2) {
				float x = uvs[i];
				float y = uvs[i + 1];
				
				x = x / (WIDTH + 0.0f) + x1;
				y = y / (HEIGHT + 0.0f) + y1;
				
				uvs[i] = x;
				uvs[i + 1] = y;
				
//				xc += x;
//				yc += y;
			}
			
			// Remove edges picking the wrong color
//			float epsilon = - (1 / 64.0f);
//			xc /= len * 0.5f;
//			yc /= len * 0.5f;
//			
//			for(int i = 0; i < len; i += 2) {
//				float dx = (uvs[i] - xc) * epsilon;
//				float dy = (uvs[i + 1] - yc) * epsilon;
//				
//				uvs[i] += dx;
//				uvs[i + 1] += dy;
//			}
		}
	}
}
