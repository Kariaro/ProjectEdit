package com.hardcoded.lwjgl.icon;

import com.hardcoded.api.IResource;
import com.hardcoded.lwjgl.data.TextureAtlas;

public class TextureManager extends IResource {
	private IconGenerator iconGenerator;
	private TextureAtlas blockAtlas;
	
	public TextureManager() {
		iconGenerator = new IconGenerator(this);
		blockAtlas = new TextureAtlas(); 
	}
	
	public IconGenerator getIconGenerator() {
		return iconGenerator;
	}
	
	public TextureAtlas getBlockAtlas() {
		return blockAtlas;
	}
	
	@Override
	public void init() {
		iconGenerator.init();
	}
}
