package com.hardcoded.lwjgl.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Debug version of the texture atlas
 */
public class TextureAtlasDebugger extends TextureAtlas {
	
	private JFrame frame;
	private JLabel label;
	
	public TextureAtlasDebugger(int padding, int align, int width, int height, boolean fillPadding, int interpolation) {
		super(padding, align, width, height, fillPadding, interpolation);
		
		frame = new JFrame("TextureAtlasDebugger - Preview");
		frame.setSize(1034, 1034);
		JPanel panel = new JPanel();
		panel.setLayout(null);
		label = new JLabel(new ImageIcon(image));
		Dimension dim = new Dimension(1034, 1034);
		frame.setBackground(Color.black);
		label.setPreferredSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
		label.setSize(dim);
		panel.add(label);
		panel.setPreferredSize(dim);
		frame.setContentPane(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
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
		
		try {
			BufferedImage bi2 = new BufferedImage(1034, 1034, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gr = bi2.createGraphics();
			gr.drawImage(image, 5, 5, null);
			gr.setColor(Color.black);
			gr.drawRect(5, 5, 1024, 1024);
			
			{
				int ix = id % ATLAS_MAP_WIDTH;
				int iy = id / ATLAS_MAP_WIDTH;
				
				int ixp = ix * ALIGN;
				int iyp = iy * ALIGN;
				
				gr.setColor(Color.red);
				gr.fillRect(5 + ixp, 5 + iyp, aw * ALIGN, ah * ALIGN);
			}
			
			gr.setColor(new Color(0, 0, 0, 0.3f));
			for(int id_v : atlas_path_to_id.values()) {
				AtlasUv id_uv = getUv(id_v);
				
				int id_x = (int)(id_uv.x0 * WIDTH);
				int id_y = (int)(id_uv.y0 * HEIGHT);
				int id_w = (int)((id_uv.x1 - id_uv.x0) * WIDTH);
				int id_h = (int)((id_uv.y1 - id_uv.y0) * HEIGHT);
				gr.drawRect(id_x + 5, id_y + 5, id_w, id_h);
			}
			
			label.setIcon(new ImageIcon(bi2));
			label.repaint();
			frame.repaint();
//			Thread.sleep(5);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}
}
