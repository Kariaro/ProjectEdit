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
	
	/**
	 * This is the size of each box created inside this atlas
	 */
	private static final int ALIGN = 16;
	
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
	private static final int PADDING_PIXELS = 8;
	
	private static final int ATLAS_MAP_WIDTH = WIDTH / ALIGN;
	private static final int ATLAS_MAP_HEIGHT = HEIGHT / ALIGN;
	
	private final AtlasUv[] atlas_map = new AtlasUv[ATLAS_MAP_WIDTH * ATLAS_MAP_HEIGHT];
	private final Map<String, Integer> atlas_path_to_id;
	private final BufferedImage image;
	private Texture atlas;
	
//	private JFrame frame;
//	private JLabel label;
	public TextureAtlas() {
		this.image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		this.atlas_path_to_id = new HashMap<>();
		
//		frame = new JFrame("Test Atlas");
//		frame.setSize(1034, 1034);
//		JPanel panel = new JPanel();
//		panel.setLayout(null);
//		label = new JLabel(new ImageIcon(image));
//		Dimension dim = new Dimension(1034, 1034);
//		frame.setBackground(Color.black);
//		label.setPreferredSize(dim);
//		label.setMinimumSize(dim);
//		label.setMaximumSize(dim);
//		label.setSize(dim);
//		panel.add(label);
//		panel.setPreferredSize(dim);
//		frame.setContentPane(panel);
//		frame.pack();
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setResizable(false);
//		frame.setVisible(true);
	}
	
	public synchronized int addTexture(String path, BufferedImage bi) {
		if(atlas_path_to_id.containsKey(path)) {
			return atlas_path_to_id.get(path);
		}
		
		int id = findSpace(bi.getWidth(), bi.getHeight());
		if(id < 0) {
//			System.out.println("Failed");
//			try {
//				Scanner scanner = new Scanner(System.in);
//				
//				while(true) {
//					String str = scanner.next();
//					int num = 1;
//					try {
//						num = Integer.valueOf(str);
//					} catch(Exception e) {
//						e.printStackTrace();
//						continue;
//					}
//					
//					int bi_width = num;
//					int bi_height = num;
//					
//					id = findSpace(bi_width, bi_height);
//					
//					int aw = (bi_width + PADDING_PIXELS * 2 + ALIGN - 1) / ALIGN;
//					int ah = (bi_height + PADDING_PIXELS * 2 + ALIGN - 1) / ALIGN;
//					
//					try {
//						BufferedImage bi2 = new BufferedImage(1034, 1034, BufferedImage.TYPE_INT_ARGB);
//						Graphics2D gr = bi2.createGraphics();
//						gr.drawImage(image, 5, 5, null);
//						gr.setColor(Color.black);
//						gr.drawRect(5, 5, 1024, 1024);
//						
//						{
//							int ix = id % ATLAS_MAP_WIDTH;
//							int iy = id / ATLAS_MAP_WIDTH;
//							
//							int ixp = ix * ALIGN;
//							int iyp = iy * ALIGN;
//							
//							gr.setColor(Color.red);
//							gr.fillRect(5 + ixp, 5 + iyp, aw * ALIGN, ah * ALIGN);
//						}
//						
//						label.setIcon(new ImageIcon(bi2));
//						label.repaint();
//						frame.repaint();
//						Thread.sleep(1);
//					} catch(InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
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
		
//		try {
//			BufferedImage bi2 = new BufferedImage(1034, 1034, BufferedImage.TYPE_INT_ARGB);
//			Graphics2D gr = bi2.createGraphics();
//			gr.drawImage(image, 5, 5, null);
//			gr.setColor(Color.black);
//			gr.drawRect(5, 5, 1024, 1024);
//			
//			{
//				int ix = id % ATLAS_MAP_WIDTH;
//				int iy = id / ATLAS_MAP_WIDTH;
//				
//				int ixp = ix * ALIGN;
//				int iyp = iy * ALIGN;
//				
//				gr.setColor(Color.red);
//				gr.fillRect(5 + ixp, 5 + iyp, aw * ALIGN, ah * ALIGN);
//			}
//			
//			label.setIcon(new ImageIcon(bi2));
//			label.repaint();
//			frame.repaint();
//			Thread.sleep(5);
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//		}
		
		atlas_path_to_id.put(path, id);
		return id;
	}
	
	private void applyImage(Graphics2D g, BufferedImage bi, int xp, int yp) {
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
	
	/**
	 * Find the first index inside this atlas that has enough space
	 * to draw an image with the dimensions {@code width} x {@code height} pixels.
	 * 
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @return the index or {@code -1} if no index was found
	 */
	private int findSpace(int width, int height) {
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
		
//		System.out.printf("size: (%d x %d)\n", tw, th);
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
