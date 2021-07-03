package com.hardcoded.render.menubar;

import java.util.ArrayList;
import java.util.List;

import com.hardcoded.render.menubar.IMenuItem.IPopupEntry;
import com.hardcoded.render.menubar.MenuGraphics.ButtonTexture;

public class IMenuGroup {
	MenuBar menuBar;
	List<IMenuItem> list;
	ButtonTexture tex;
	int width;
	
	public IMenuGroup(MenuBar menuBar, String name, int width) {
		this.menuBar = menuBar;
		this.tex = menuBar.menuGraphics.getMenuButton(name);
		this.list = new ArrayList<>();
		this.width = width;
	}
	
	private IMenuGroup() {
		
	}
	
	public int getPopupWidth() {
		return width;
	}
	
	public int getPopupHeight() {
		return 6 + list.size() * 22;
	}
	
	public IMenuGroup add(String name, Runnable action) {
		return add(name, action, 0, 0);
	}
	
	public IMenuGroup add(String name, Runnable action, int key, int modifier) {
		list.add(new IMenuItem(menuBar, name, width, key, modifier, action));
		return this;
	}
	
	static class IMenuEntry extends IMenuGroup {
		private final String name;
		private final IMenuItem[] elements;
		
		protected IMenuEntry(String name, IMenuItem[] elements) {
			this.list = null;
			this.tex = null;
			this.width = 0;
			this.name = name;
			this.elements = elements;
		}
		
		protected IMenuGroup build(MenuBar menuBar, int width) {
			IMenuGroup item = new IMenuGroup(menuBar, name, width);
			
			for(IMenuItem element : elements) {
				IPopupEntry entry = (IPopupEntry)element;
				item.list.add(entry.build(menuBar, width));
			}
			
			return item;
		}
	}
	
	public static IMenuGroup of(String name, IMenuItem... elements) {
		return new IMenuEntry(name, elements);
	}
}
