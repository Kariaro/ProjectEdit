package com.hardcoded.render.gui;

import org.lwjgl.opengl.GL11;

import com.hardcoded.api.IResource;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.render.LwjglRender;
import com.hardcoded.render.gui.components.GuiBlockMenu;
import com.hardcoded.render.gui.components.GuiToolList;

public class GuiRender extends IResource {
	protected LwjglRender render;
	private GuiToolList tools;
	private GuiPanel panel;
	
	public IBlockData selectedBlock;
	
	public GuiRender(LwjglRender render) {
		this.render = render;
	}
	
	@Override
	public void init() {
		try {
			GuiLabel.createTextAtlas();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		panel = new GuiPanel();
		
		tools = new GuiToolList(this);
		tools.setLocation(0, 0);
		
		GuiBlockMenu blockMenu = new GuiBlockMenu(this);
		panel.add(tools);
		
		blockMenu.setBounds(72, 0, 500, 500);
		panel.add(blockMenu);
	}
	
	public void tick() {
		if(panel == null) return;
		
		if(!LwjglWindow.isMouseCaptured()) {
			panel.fireTick();
		}
	}
	
	public void render() {
		if(panel == null) return;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1, 1, 1, 1);
		panel.render();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public boolean isInside(int x, int y, int w, int h) {
		float mx = Input.getMouseX();
		float my = Input.getMouseY();
		return !(mx < x || my < y || mx > x + w || my > y + h);
	}
}
