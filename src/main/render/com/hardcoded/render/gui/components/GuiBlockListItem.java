package com.hardcoded.render.gui.components;

import org.lwjgl.opengl.GL11;

import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.render.gui.GuiComponent;
import com.hardcoded.render.gui.GuiImage;

class GuiBlockListItem extends GuiComponent {
	private GuiImage image;
	
	public GuiBlockListItem() {
		setSize(64, 64);
		
		image = new GuiImage();
		image.setSize(64, 64);
	}
	
	public GuiBlockListItem setBlock(IBlockData data) {
		image.setTexture(ProjectEdit.getInstance().getTextureManager().getIconGenerator().getTextureAtlas(), data.getName());
//		label.setText("minecraft:" + data.getName());
		this.block = data;
		return this;
	}
	
	public boolean hover;
	public boolean selected;
	private IBlockData block;
	
	public IBlockData getBlock() {
		return block;
	}
	
	@Override
	public void renderComponent() {
		int x = getX();
		int y = getY();
		
		if(selected) {
			GL11.glColor4f(0, 0, 0, 0.7f);
			renderBox(x, y, 64, 64);
		} else if(hover) {
			GL11.glColor4f(0, 0, 0, 0.3f);
			renderBox(x, y, 64, 64);
		}
		
		GL11.glColor4f(1, 1, 1, 1);
		image.setLocation(x, y);
		image.renderComponent();
	}
	
	@Override
	public int getHeight() {
		return 64;
	}
}
