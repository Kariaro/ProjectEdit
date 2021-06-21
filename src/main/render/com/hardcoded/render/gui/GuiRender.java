package com.hardcoded.render.gui;

import org.lwjgl.opengl.GL11;

import com.hardcoded.api.IResource;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiKeyEvent;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiMouseEvent;
import com.hardcoded.render.gui.components.GuiBlockMenu;
import com.hardcoded.render.gui.components.GuiToolList;

public class GuiRender extends IResource {
	private GuiToolList tools;
	private GuiPanel panel;
	
	public IBlockData selectedBlock;
	
	public GuiRender() {
	}
	
	@Override
	public void init() {
		try {
			GuiLabel.createTextAtlas();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		panel = new GuiPanel();
		
		GuiBlockMenu blockMenu = new GuiBlockMenu(this);
		blockMenu.setBounds(72, 0, 500, 500);
		panel.add(blockMenu);
		
		tools = new GuiToolList(this);
		tools.setLocation(0, 0);
		tools.setSize(72, 72 * 8);
		panel.add(tools);
	}
	
	public void tick() {
		if(panel == null) return;
	}
	
	public void render() {
		if(panel == null) return;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 1);
		panel.render();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public boolean isInside(int x, int y, int w, int h) {
		float mx = Input.getMouseX();
		float my = Input.getMouseY();
		return !(mx < x || my < y || mx > x + w || my > y + h);
	}

	public GuiListener processMouseEvent(GuiMouseEvent event) {
		return panel.processMouseEvent(event, true);
	}

	public void processKeyEvent(GuiKeyEvent event) {
		panel.processKeyEvent(event);
	}
}
