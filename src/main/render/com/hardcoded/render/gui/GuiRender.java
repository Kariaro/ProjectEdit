package com.hardcoded.render.gui;

import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.render.LwjglRender;

public class GuiRender {
	private GuiToolList tools;
	protected LwjglRender render;
	
	public GuiRender(LwjglRender render) {
		this.render = render;
		tools = new GuiToolList(this);
	}
	
	public void render() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor3f(1, 1, 1);
		tools.render();
	}
	
	protected boolean isInside(int x, int y, int w, int h) {
		float mx = Input.getMouseX();
		float my = Input.getMouseY();
		return !(mx < x || my < y || mx > x + w || my > y + h);
	}
}
