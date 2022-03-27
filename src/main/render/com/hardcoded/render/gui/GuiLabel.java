package com.hardcoded.render.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;

public class GuiLabel extends GuiComponent {
	protected static TextureAtlas text_atlas;
	private static Map<Character, Integer> glyph_map;
	private static final String GLYPHS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ._- *()[]{}?|+/\\.,<>':";
	private static final char[] GLYPH_ARRAY = GLYPHS.toCharArray();
	private static final int GLYPH_SIZE = 48;
	
	protected static void createTextAtlas() throws Exception {
		if(text_atlas == null) {
			text_atlas = new TextureAtlas(2, 16, 1024, 1024, false);
		} else {
			text_atlas.unload();
		}
		
		glyph_map = new HashMap<>();
		
		Font font = Font.createFont(Font.TRUETYPE_FONT, GuiLabel.class.getResourceAsStream("/fonts/Consolas.ttf"));
		font = font.deriveFont(48.0f);
		
		BufferedImage bi = new BufferedImage(GLYPH_SIZE, GLYPH_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		
		g.setBackground(new Color(0, true));
		g.setColor(Color.white);
		for(char c : GLYPH_ARRAY) {
			g.clearRect(0, 0, GLYPH_SIZE, GLYPH_SIZE);
			String str = Character.toString(c);
			float x = (GLYPH_SIZE - metrics.stringWidth(str)) / 2.0f;
			float y = (GLYPH_SIZE - metrics.getHeight()) / 2.0f + (float)metrics.getAscent();
			
			g.drawString(str, x, y);
			glyph_map.put(c, text_atlas.addTexture(str, bi));
		}
		
		g.dispose();
		
		text_atlas.reload();
	}
	
	private float font_size = 20;
	private Vector4f color = new Vector4f(1, 1, 1, 1);
	private String text;
	
	public GuiLabel() {
		
	}
	
	public GuiLabel(String text) {
		this.text = text;
	}
	
	public GuiLabel setText(String text) {
		this.text = text;
		return this;
	}
	
	public GuiLabel setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
		return this;
	}
	
	public GuiLabel setFontSize(float size) {
		this.font_size = size;
		return this;
	}
	
	@Override
	public void renderComponent() {
		String msg = text;
		if(msg == null) return;
		
		text_atlas.bind();
		
		float fs = font_size;
		float sp = (fs / 2.0f) - 1;
		float xo = getX();
		float yo = getY();
		GL11.glColor4f(color.x, color.y, color.z, color.w);
		for(int i = 0, len = msg.length(); i < len; i++, xo += fs - sp) {
			Integer id = glyph_map.get(msg.charAt(i));
			if(id == null) {
				continue;
			}
			
			AtlasUv uv = text_atlas.getUv(id);
			
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2f(uv.x0, uv.y0); GL11.glVertex2f(xo   , yo   );
				GL11.glTexCoord2f(uv.x1, uv.y0); GL11.glVertex2f(xo+fs, yo   );
				GL11.glTexCoord2f(uv.x1, uv.y1); GL11.glVertex2f(xo+fs, yo+fs);
				GL11.glTexCoord2f(uv.x0, uv.y1); GL11.glVertex2f(xo   , yo+fs);
			GL11.glEnd();
		}
		
		text_atlas.unbind();
	}
}
