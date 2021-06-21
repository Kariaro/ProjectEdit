package com.hardcoded.render.gui.components;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.render.gui.*;

public class GuiBlockListItem extends GuiComponent {
	private GuiImage image;
//	private GuiLabel label;
	
	public GuiBlockListItem() {
		image = new GuiImage();
		image.setSize(64, 64);
		
//		label = new GuiLabel();
//		label.setFontSize(24);
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
	public void tick() {
		hover = isInside(x, y, 64, 64);
		
		if(Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			if(hover) {
				selected = true;
			} else {
				selected = false;
			}
		}
	}
	
	@Override
	public void render() {
		if(hover) {
			GL11.glColor4f(0, 0, 0, 0.3f);
			renderBox(x, y, 64, 64);
		} else if(selected) {
			GL11.glColor4f(0, 0, 0, 0.7f);
			renderBox(x, y, 64, 64);
		}
		
		GL11.glColor4f(1, 1, 1, 1);
		image.setLocation(x, y);
		image.render();
	}
	
	@Override
	public int getHeight() {
		return 64;
	}
}
