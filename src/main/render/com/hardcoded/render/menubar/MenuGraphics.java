package com.hardcoded.render.menubar;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;

public class MenuGraphics {
	private static final int HOVER_OUTLINE = 0xcce8ff;
	private static final int HOVER_FILL    = 0xe5f3ff;
	private static final int PRESS_OUTLINE = 0x99d1ff;
	private static final int PRESS_FILL    = 0xcce8ff;
	private static final int NORMAL_FILL   = 0xffffff;
	private static final int NORMAL_FG     = 0x000000;
	private static final int DISABLED_FG   = 0x6d6d6d;
	
	static final int POPUP_ITEM_HOVER    = 0x91c9f7;
	static final int POPUP_ITEM_NORMAL   = 0xf2f2f2;
	static final int POPUP_ITEM_DISABLED = 0xe6e6e6;
	static final int POPUP_MENU_FG       = 0x000000;
	static final int POPUP_MENU_DISABLED = 0x6d6d6d;
	static final int POPUP_MENU_OUTLINE  = 0xcccccc;
	static final int POPUP_MENU_FILL     = 0xf2f2f2;
	
	static final int POPUP_NORMAL_EXTRA  = 0xf0f0f0;
	static final int POPUP_HOVER_EXTRA   = 0x90c8f6;
	
	
	private BufferedImage temp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	private Map<String, ButtonTexture> buttons = new HashMap<>();
	protected TextureAtlas gui_atlas;
	private Font font;
	public MenuGraphics() {
		font = Font.decode("Segoe UI");
		gui_atlas = new TextureAtlas(0, 1, 1024, 1024, false, GL11.GL_NEAREST);
	}
	
	public ButtonTexture getMenuButton(String text) {
		ButtonTexture tex = buttons.get("menu_" + text);
		if(tex != null) return tex;
		
		BufferedImage press = getMenuButtonImage(text, new Color(NORMAL_FG), new Color(PRESS_FILL), new Color(PRESS_OUTLINE));
		BufferedImage hover = getMenuButtonImage(text, new Color(NORMAL_FG), new Color(HOVER_FILL), new Color(HOVER_OUTLINE));
		BufferedImage normal = getMenuButtonImage(text, new Color(NORMAL_FG), new Color(NORMAL_FILL), new Color(NORMAL_FILL));
		BufferedImage disabled = getMenuButtonImage(text, new Color(DISABLED_FG), new Color(NORMAL_FILL), new Color(NORMAL_FILL));
		
		int uv_0 = gui_atlas.addTexture("menu_" + text + "_press", press);
		int uv_1 = gui_atlas.addTexture("menu_" + text + "_hover", hover);
		int uv_2 = gui_atlas.addTexture("menu_" + text + "_normal", normal);
		int uv_3 = gui_atlas.addTexture("menu_" + text + "_disabled", disabled);
		
		buttons.put(text, tex = new ButtonTexture(
			gui_atlas.getUv(uv_0),
			gui_atlas.getUv(uv_1),
			gui_atlas.getUv(uv_2),
			gui_atlas.getUv(uv_3),
			normal.getWidth()
		));
		
		return tex;
	}
	
	private String getKeybindString(int key, int mod) {
		if(key == 0 && mod == 0) return "";
		
		String mods = "";
		if((mod & GLFW.GLFW_MOD_CONTROL) != 0) mods += "Ctrl+";
		if((mod & GLFW.GLFW_MOD_ALT) != 0) mods += "Alt+";
		if((mod & GLFW.GLFW_MOD_SHIFT) != 0) mods += "Shift+";
		
		String keys = GLFW.glfwGetKeyName(key, GLFW.GLFW_KEY_UNKNOWN);
		
		if(keys == null) {
			switch(key) {
				case GLFW.GLFW_KEY_DELETE:
					keys = "Delete";
					break;
				case GLFW.GLFW_KEY_ENTER:
					keys = "Enter";
					break;
				default:
					keys = String.format("Unknown 0x%x", key);
					break;
			}
		}
		
		if(keys.length() == 1) {
			keys = keys.toUpperCase();
		}
		
		return mods + keys;
	}
	
	public ButtonTexture getPopupButton(String text, int width, int key, int modifiers) {
		String keybind = getKeybindString(key, modifiers);
		ButtonTexture tex = buttons.get("popup_" + text + keybind);
		if(tex != null) return tex;
		
		BufferedImage hover = getPopupButtonImage(text, keybind, width - 6, new Color(POPUP_MENU_FG), new Color(POPUP_ITEM_HOVER), new Color(POPUP_HOVER_EXTRA));
		BufferedImage normal = getPopupButtonImage(text, keybind, width - 6, new Color(POPUP_MENU_FG), new Color(POPUP_ITEM_NORMAL), new Color(POPUP_NORMAL_EXTRA));
		BufferedImage disabled = getPopupButtonImage(text, keybind, width - 6, new Color(POPUP_MENU_DISABLED), new Color(POPUP_ITEM_DISABLED), new Color(POPUP_NORMAL_EXTRA));
		
		int uv_1 = gui_atlas.addTexture("popup_" + text + keybind + "_hover", hover);
		int uv_2 = gui_atlas.addTexture("popup_" + text + keybind + "_normal", normal);
		int uv_3 = gui_atlas.addTexture("popup_" + text + keybind + "_disabled", disabled);
		
		buttons.put(text, tex = new ButtonTexture(
			gui_atlas.getUv(uv_1),
			gui_atlas.getUv(uv_1),
			gui_atlas.getUv(uv_2),
			gui_atlas.getUv(uv_3),
			normal.getWidth()
		));
		
		return tex;
	}
	
	public void reload() {
		gui_atlas.unload();
		buttons.clear();
	}
	
	public void bind() {
		gui_atlas.bind();
	}
	
	public void unbind() {
		gui_atlas.unbind();
	}
	
	private int getStringWidth(String text, Font font) {
		Graphics2D g = temp.createGraphics();
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		Rectangle2D rect = metrics.getStringBounds(text, g);
		g.dispose();
		
		return (int)Math.ceil(rect.getWidth());
	}
	
	private BufferedImage getPopupButtonImage(String text, String keybind, int width, Color fg, Color bg, Color extra) {
		Font font = this.font.deriveFont(12.0f);
		
		int font_width = getStringWidth(text, font);
		BufferedImage img = new BufferedImage(width, 22, BufferedImage.TYPE_INT_ARGB);
		BufferedImage textImage = getText(font, text, fg, bg, font_width, 22);
		
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(bg);
		g.fillRect(0, 0, width, 22);
		g.drawImage(textImage, 32, -1, null);
		g.setColor(extra);
		g.fillRect(0, 0, 28, 22);
		if(!keybind.isEmpty()) {
			int text_width = getStringWidth(keybind, font);
			BufferedImage mnemonicImage = getText(font, keybind, fg, bg, text_width, 22);
			g.drawImage(mnemonicImage, width - mnemonicImage.getWidth() - 18, -1, null);
		}
		
		g.dispose();
		
		return img;
	}
	
	private BufferedImage getMenuButtonImage(String text, Color fg, Color bg, Color outline) {
		Font font = this.font.deriveFont(12.0f);
		
		int width = getStringWidth(text, font);
		BufferedImage img = new BufferedImage(width + 10, 18, BufferedImage.TYPE_INT_ARGB);
		BufferedImage textImage = getText(font, text, fg, bg, width, 18);
		
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(bg);
		g.fillRect(0, 0, width + 10, 18);
		g.drawImage(textImage, 5, 0, null);
		g.setColor(outline);
		g.drawRect(0, 0, width + 9, 17);
		g.dispose();
		
		return img;
	}
	
	private BufferedImage getText(Font font, String text, Color fg, Color bg, int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setColor(bg);
		g.fillRect(0, 0, width, height);
		g.setColor(fg);
		g.setFont(font);
		
		FontMetrics metrics = g.getFontMetrics();
		Rectangle2D rect = metrics.getStringBounds(text, g);
		
		float xo = (float)((width - rect.getWidth()) / 2.0);
		float yo = (float)((height - rect.getHeight()) / 2.0);
		g.drawString(text, xo, yo + metrics.getAscent());
		g.dispose();
		
		return bi;
	}
	
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_PRESS = 1;
	public static final int TYPE_HOVER = 2;
	public static final int TYPE_DISABLED = 3;
	
	public class ButtonTexture {
		private final AtlasUv[] array;
		public final int width;
		
		public ButtonTexture(AtlasUv press, AtlasUv hover, AtlasUv normal, AtlasUv disabled, int width) {
			array = new AtlasUv[] { normal, press, hover, disabled };
			this.width = width;
		}
		
		public AtlasUv get(int type) {
			return array[type];
		}
	}
}
