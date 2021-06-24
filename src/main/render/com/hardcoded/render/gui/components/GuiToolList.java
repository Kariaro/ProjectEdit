package com.hardcoded.render.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.lwjgl.icon.GuiIcons;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.render.gui.GuiComponent;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiKeyEvent;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiMouseEvent;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiMouseMove;
import com.hardcoded.render.gui.GuiRender;

public class GuiToolList extends GuiComponent implements GuiListener {
	private int selectedIndex = -1;
	private int hoverIndex = -1;
	
	private List<GuiTool> tools;
	private GuiRender gui;
	private GuiIcons icons;
	
	public GuiToolList(GuiRender gui, int x, int y) {
		this.gui = gui;
		this.icons = ProjectEdit.getInstance().getTextureManager().getGuiIcons();
		this.tools = new ArrayList<>();
		this.setLocation(x, y);
		
		addTool(GuiToolEmpty::new);
		addTool(GuiToolSelection::new);
	}
	
	protected GuiToolList addTool(BiFunction<GuiRender, Integer, GuiTool> generator) {
		GuiTool tool = generator.apply(gui, tools.size());
		tool.setBounds(getX(), getY() + tools.size() * 72, 72, 72);
		tools.add(tool);
		return this;
	}
	
	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		if(event.isConsumed()) return;
		event.requestFocus();
		
		if(event instanceof GuiMouseMove) {
			hoverIndex = -1;
			if(!LwjglWindow.isMouseCaptured()) {
				for(GuiTool tool : tools) {
					if(event.isInside(tool)) {
						hoverIndex = tool.getIndex();
					}
				}
			}
		}
		
		if(event.isInside(this)) {
			event.requestFocus();
		}
		
		if(selectedIndex >= 0) {
			GuiTool tool = tools.get(selectedIndex);
			tool.onMouseEvent(event);
		}
		
		if(!LwjglWindow.isMouseCaptured()) {
			boolean mouse = event.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1);
			
			for(GuiTool tool : tools) {
				if(event.isInside(tool) && mouse) {
					selectedIndex = tool.getIndex();
				}
			}
		}
	}
	
	@Override
	public void onKeyEvent(GuiKeyEvent event) {
		if(selectedIndex >= 0) {
			GuiTool tool = tools.get(selectedIndex);
			tool.onKeyEvent(event);
		}
	}
	
	@Override
	public void renderComponent() {
		int x = getX();
		int y = getY();
		
		for(int i = 0, len = tools.size(); i < len; i++) {
			GuiTool tool = tools.get(i);
			tool.selected = i == selectedIndex;
			renderBox(i, x, y + i * 72, 72, 72);
			tool.renderComponent();
		}
	}
	
	public void renderBox(int id, int x, int y, int w, int h) {
		Texture tex = icons.gui_box_normal;
		
		if(id == selectedIndex) {
			tex = icons.gui_box_selected;
		} else if(id == hoverIndex) {
			tex = icons.gui_box_hover;
		}
		
		tex.bind();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2d(0, 0); GL11.glVertex2i(x  , y  );
			GL11.glTexCoord2d(1, 0); GL11.glVertex2i(x+w, y  );
			GL11.glTexCoord2d(1, 1); GL11.glVertex2i(x+w, y+h);
			GL11.glTexCoord2d(0, 1); GL11.glVertex2i(x  , y+h);
		GL11.glEnd();
		
		tex.unbind();
	}
}
