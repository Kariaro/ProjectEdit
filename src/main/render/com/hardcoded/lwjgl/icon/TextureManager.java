package com.hardcoded.lwjgl.icon;

import com.hardcoded.api.IResource;
import com.hardcoded.api.ResourceException;
import com.hardcoded.lwjgl.data.TextureAtlas;

public class TextureManager extends IResource {
	private IconGenerator iconGenerator;
	private TextureAtlas blockAtlas;
	private GuiIcons guiIcons;
	
	public TextureManager() {
		iconGenerator = new IconGenerator(this);
		blockAtlas = new TextureAtlas();
		guiIcons = new GuiIcons();
	}
	
	public IconGenerator getIconGenerator() {
		return iconGenerator;
	}
	
	public TextureAtlas getBlockAtlas() {
		return blockAtlas;
	}
	
	public GuiIcons getGuiIcons() {
		return guiIcons;
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
