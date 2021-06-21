package com.hardcoded.render.gui.components;

import com.hardcoded.render.gui.GuiComponent;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiRender;

public abstract class GuiTool extends GuiComponent implements GuiListener {
	protected final GuiRender gui;
	protected boolean selected;
	private final int index;
	
	protected GuiTool(GuiRender gui, int index) {
		this.gui = gui;
		this.index = index;
	}
	
	@Override
	protected final void renderComponent() {
		renderGui();
		renderWorld();
	}
	
	public int getIndex() {
		return index;
	}
	
	protected abstract void renderGui();
	protected abstract void renderWorld();
}
