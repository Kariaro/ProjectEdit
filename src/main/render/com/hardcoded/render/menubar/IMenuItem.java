package com.hardcoded.render.menubar;

import com.hardcoded.render.menubar.MenuGraphics.ButtonTexture;

public class IMenuItem {
	ButtonTexture tex;
	Runnable action;
	int key, modifier;
	
	protected IMenuItem(MenuBar menuBar, String name, int width, int key, int modifier, Runnable action) {
		this.tex = menuBar.menuGraphics.getPopupButton(name, width, key, modifier);
		this.action = action;
		this.key = key;
		this.modifier = modifier;
	}
	
	private IMenuItem() {
		
	}
	
	static class IPopupEntry extends IMenuItem {
		private final String name;
		
		protected IPopupEntry(String name, int key, int modifier, Runnable action) {
			this.name = name;
			this.key = key;
			this.modifier = modifier;
			this.action = action;
		}
		
		protected IMenuItem build(MenuBar menuBar, int width) {
			return new IMenuItem(menuBar, name, width, key, modifier, action);
		}
	}
	
	public static IMenuItem of(String name, Runnable action) {
		return new IPopupEntry(name, 0, 0, action);
	}
	
	public static IMenuItem of(String name, Runnable action, int key) {
		return new IPopupEntry(name, key, 0, action);
	}
	
	public static IMenuItem of(String name, Runnable action, int key, int modifier) {
		return new IPopupEntry(name, key, modifier, action);
	}
}