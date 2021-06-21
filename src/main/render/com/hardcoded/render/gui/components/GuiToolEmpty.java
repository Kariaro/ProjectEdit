package com.hardcoded.render.gui.components;

import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiKeyEvent;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiMouseEvent;
import com.hardcoded.render.gui.GuiRender;

public class GuiToolEmpty extends GuiTool {
	
	protected GuiToolEmpty(GuiRender gui, int index) {
		super(gui, index);
	}

	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		
	}

	@Override
	public void onKeyEvent(GuiKeyEvent event) {
		
	}

	@Override
	protected void renderGui() {
		
	}

	@Override
	protected void renderWorld() {
		
	}
}
