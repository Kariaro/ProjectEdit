package com.hardcoded.render.gui;

public class GuiPanel extends GuiComponent {
	@Override
	public void renderComponent() {
		for(GuiComponent comp : children) {
			comp.renderComponent();
		}
	}
	
	@Override
	public void add(GuiComponent comp) {
		super.add(comp);
	}
}
