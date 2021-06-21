package com.hardcoded.lwjgl.data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.hardcoded.api.IResource;

public class TextureAtlas extends IResource {
	protected final int WIDTH;
	protected final int HEIGHT;
	
	/**
	 * This is the size of each box created inside this atlas
	 */
	protected final int ALIGN;
	
	/**
	 * This is the padding amount for each texture in the atlas.
	 * 
	 * <pre>
	 *  C: corner padding
	 *  E: edge padding
	 *  
	 *  C C E E E C C
	 *  C C E E E C C
	 *  E E . . . E E
	 *  E E . . . E E
	 *  E E . . . E E
	 *  C C E E E C C
	 *  C C E E E C C
	 * </pre>
	 */
	protected final int PADDING_PIXELS;
	
	protected final int ATLAS_MAP_WIDTH;
	protected final int ATLAS_MAP_HEIGHT;
	
	protected final AtlasUv[] atlas_map;
	protected final Map<String, Integer> atlas_path_to_id;
	protected final BufferedImage image;
	protected Texture atlas;
	
	public TextureAtlas() {
		this(8, 16, 2048, 2048);
	}
	
	public TextureAtlas(int padding, int align, int width, int height) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this.atlas_path_to_id = new HashMap<>();
		this.PADDING_PIXELS = padding;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.ALIGN = align;
		this.ATLAS_MAP_WIDTH = WIDTH / ALIGN;
		this.ATLAS_MAP_HEIGHT = HEIGHT / ALIGN;
		this.atlas_map = new AtlasUv[ATLAS_MAP_WIDTH * ATLAS_MAP_HEIGHT];
	}
	
	public synchronized int addTexture(String path, BufferedImage bi) {
		if(atlas_path_to_id.containsKey(path)) {
			return atlas_path_to_id.get(path);
		}
		
		int id = findSpace(bi.getWidth(), bi.getHeight());
		if(id < 0) {
			throw new ArrayIndexOutOfBoundsException("Atlas map could not fit texture: bi[ width=" + bi.getWidth() + ", height=" + bi.getHeight() + "]; " + bi);
		}
		
		int aw = (bi.getWidth() + PADDING_PIXELS * 2 + ALIGN - 1) / ALIGN;
		int ah = (bi.getHeight() + PADDING_PIXELS * 2 + ALIGN - 1) / ALIGN;
		Graphics2D g = image.createGraphics();
		int xp = id % ATLAS_MAP_WIDTH;
		int yp = id / ATLAS_MAP_WIDTH;
		
		applyImage(g, bi, xp, yp);
		g.dispose();
		
		AtlasUv uv = new AtlasUv(id,
			(xp * ALIGN + PADDING_PIXELS) / (WIDTH + 0.0f),
			(yp * ALIGN + PADDING_PIXELS) / (HEIGHT + 0.0f),
			(xp * ALIGN + PADDING_PIXELS + bi.getWidth()) / (WIDTH + 0.0f),
			(yp * ALIGN + PADDING_PIXELS + bi.getHeight()) / (HEIGHT + 0.0f)
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
	
	protected void applyImage(Graphics2D g, BufferedImage bi, int xp, int yp) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		int p = PADDING_PIXELS;
		int x1 = xp * ALIGN + p;
		int y1 = yp * ALIGN + p;
		int x2 = x1 + w;
		int y2 = y1 + h;
		
		// . # .
		// #   #
		// . # .
		// Fill Edges
		g.drawImage(bi, x1 - p, y1, x1, y2, 0, 0, 1, h, null);
		g.drawImage(bi, x2, y1, x2 + p, y2, w - 1, 0, w, h, null);
		g.drawImage(bi, x1, y1 - p, x2, y1, 0, 0, w, 1, null);
		g.drawImage(bi, x1, y2, x2, y2 + p, 0, h - 1, w, h, null);
		
		// # - #
		// |   |
		// # - #
		// Fill Corners
		g.drawImage(bi, x1 - p, y1 - p, x1, y1, 0, 0, 1, 1, null);
		g.drawImage(bi, x2, y1 - p, x2 + p, y1, w - 1, 0, w, 1, null);
		g.drawImage(bi, x1 - p, y2, x1, y2 + p, 0, h - 1, 1, h, null);
		g.drawImage(bi, x2, y2, x2 + p, y2 + p, w - 1, h - 1, w, h, null);
		

		g.setBackground(new Color(0, true));
		g.clearRect(x1, y1, bi.getWidth(), bi.getHeight());
		g.drawImage(bi, x1, y1, null);
	}
	
	public boolean hasImage(String path) {
		return atlas_path_to_id.containsKey(path);
	}
	
	public int getImageId(String path) {
		return atlas_path_to_id.getOrDefault(path, -1);
	}
	
	public int entries() {
		return atlas_path_to_id.size();
	}
	
	/**
	 * Remove all images loaded in this atlas and delete textures.
	 */
	@Override
	public void unload() {
		if(atlas != null) {
			atlas.cleanup();
			atlas = null;
		}
		
		// Clear map
		for(int i = 0, len = atlas_map.length; i < len; i++) {
			atlas_map[i] = null;
		}
		
		atlas_path_to_id.clear();
		
		// Clear image
		Graphics2D g = image.createGraphics();
		g.setBackground(new Color(0, true));
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		g.dispose();
	}

	@Override
	public void reload() {
		if(atlas != null) {
			atlas.cleanup();
			atlas = null;
		}
		
		atlas = Texture.loadBufferedImageTexture(image, GL11.GL_NEAREST);
	}
	
	public AtlasUv getUv(int id) {
		return atlas_map[id];
	}
	
	
	/**
	 * Find the first index inside this atlas that has enough space
	 * to draw an image with the dimensions {@code width} x {@code height} pixels.
	 * 
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @return the index or {@code -1} if no index was found
	 */
	protected int findSpace(int width, int height) {
		// Calculate the width and height of the texture if rendered on the atlas
		int wi = width + PADDING_PIXELS * 2;
		int he = height + PADDING_PIXELS * 2;
		
		// Calculate the size this image would occupy inside the atlas map
		int tw = (wi + ALIGN - 1) / ALIGN;
		int th = (he + ALIGN - 1) / ALIGN;
		
		int xe = ATLAS_MAP_WIDTH - tw;
		int ye = ATLAS_MAP_HEIGHT - th;
		
		if(tw == 1 && th == 1) {
			// Special case for single length dimensions
			for(int i = 0, len = atlas_map.length; i < len; i++) {
				if(atlas_map[i] == null) {
					return i;
				}
			}
			
			return - 1;
		}
		
		for(int y = 0; y <= ye; y++) {
			for(int x = 0; x <= xe; x++) {
				// First we need to check if we can find 'tw' empty spaces
				int idx = x + y * ATLAS_MAP_WIDTH;
				
				// Find the first empty space and find if the box is large enough
				if(atlas_map[idx] == null) {
					search_x: {
						// check if the current position has 'tw' empty spaces
						for(int i = 1; i < tw; i++) {
							if(atlas_map[idx++] != null) {
								// This space was already occupied by something else
								// Increment x by the amount searched and break
								x += i - 1;
								break search_x;
							}
						}
						
						// We found a potential match check the y values
						for(int j = y + 1, len = y + th; j < len; j++) {
							idx = x + j * ATLAS_MAP_WIDTH;
							
							for(int i = 0; i < tw; i++) {
								if(atlas_map[idx++] != null) {
									// We didn't find a match but we went past the first check
									// Increment x by the texture height and break
									x += tw - 1;
									break search_x;
								}
							}
						}
						
						// We found a match at this position
						return x + y * ATLAS_MAP_WIDTH;
					}
				}
			}
		}
		
		return -1;
	}
	
	public Texture getTexture() {
		return atlas;
	}
	
	public int getTextureId() {
		return atlas.textureId;
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
			
			// Transform from texture coordinates to this coordinates
			for(int i = 0; i < len; i += 2) {
				float x = uvs[i];
				float y = uvs[i + 1];
				
				x = x / (WIDTH + 0.0f) + x1;
				y = y / (HEIGHT + 0.0f) + y1;
				
				uvs[i] = x;
				uvs[i + 1] = y;
			}
		}
	}
}
