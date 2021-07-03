package com.hardcoded.lwjgl.icon;

import com.hardcoded.api.IResource;
import com.hardcoded.api.ResourceException;
import com.hardcoded.lwjgl.data.TextureAtlasMipmap;

public class TextureManager extends IResource {
	private IconGenerator iconGenerator;
	private TextureAtlasMipmap blockAtlas;
	private WindowIcons windowIcons;
	private GuiIcons guiIcons;
	
	public TextureManager() {
		iconGenerator = new IconGenerator(this);
		windowIcons = new WindowIcons();
//		blockAtlas = new TextureAtlas(8, 16, 8192, 8192, true);
		blockAtlas = new TextureAtlasMipmap(1024, 1024);
		guiIcons = new GuiIcons();
	}
	
	public IconGenerator getIconGenerator() {
		return iconGenerator;
	}
	
	public TextureAtlasMipmap getBlockAtlas() {
		return blockAtlas;
	}
	
	public GuiIcons getGuiIcons() {
		return guiIcons;
	}
	
	public WindowIcons getWindowIcons() {
		return windowIcons;
	}
	
	@Override
	public void init() {
		iconGenerator.init();
		guiIcons.init();
	}
	
	@Override
	public void cleanup() throws ResourceException {
		guiIcons.cleanup();
	}
}
