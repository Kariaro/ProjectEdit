package com.hardcoded.render.gui;

public class GuiPanel extends GuiComponent {
	@Override
	public void tick() {
		
	}
	
	@Override
	public void render() {
		for(GuiComponent comp : children) {
			comp.render();
		}
	}
}
