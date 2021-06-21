package com.hardcoded.lwjgl.icon;

import com.hardcoded.lwjgl.data.Texture;

import org.lwjgl.opengl.GL11;

import com.hardcoded.api.IResource;
import com.hardcoded.api.ResourceException;

public class GuiIcons extends IResource {
	public Texture tool_selection;
	
	public Texture gui_box_selected;
	public Texture gui_box_normal;
	public Texture gui_box_hover;
	
	@Override
	public void init() throws ResourceException {
		tool_selection = Texture.loadResource("/gui/tool_selection.png", GL11.GL_NEAREST);
		
		gui_box_selected = Texture.loadResource("/gui/box_selected.png", GL11.GL_NEAREST);
		gui_box_normal = Texture.loadResource("/gui/box_normal.png", GL11.GL_NEAREST);
		gui_box_hover = Texture.loadResource("/gui/box_hover.png", GL11.GL_NEAREST);
	}
	
	@Override
	public void cleanup() throws ResourceException {
		tool_selection.cleanup();
		
		gui_box_selected.cleanup();
		gui_box_normal.cleanup();
		gui_box_hover.cleanup();
	}
}
