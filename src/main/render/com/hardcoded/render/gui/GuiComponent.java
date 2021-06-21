package com.hardcoded.render.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiKeyEvent;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiMouseEvent;

public abstract class GuiComponent {
	private GuiComponent parent;
	private int width;
	private int height;
	private int x;
	private int y;
	
	protected List<GuiComponent> children;
	
	public GuiComponent() {
		this.children = List.of();
	}
	
	protected void add(GuiComponent comp) {
		if(comp == null) return;
		
		if(children.isEmpty()) {
			children = new ArrayList<>();
		}
		
		children.add(comp);
		comp.x += x;
		comp.y += x;
		comp.parent = this;
	}
	
	public GuiComponent getParent() {
		return parent;
	}
	
	public boolean isMouseInside() {
		return isInside(x, y, width, height);
	}
	
	public boolean isPressed() {
		return Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1);
	}
	
	public GuiComponent setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public GuiComponent setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}
	
	public GuiComponent setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	public static boolean isInside(float x, float y, float w, float h) {
		float mx = Input.getMouseX();
		float my = Input.getMouseY();
		return !(mx <= x || my <= y || mx > x + w || my > y + h);
	}
	
	public final void render() {
		renderComponent();
	}
	
	protected abstract void renderComponent();
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isFocused() {
		return Input.isFocused(this);
	}
	
	public void renderBox() {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2f(0, 0); GL11.glVertex2i(x      , y       );
			GL11.glTexCoord2f(1, 0); GL11.glVertex2i(x+width, y       );
			GL11.glTexCoord2f(1, 1); GL11.glVertex2i(x+width, y+height);
			GL11.glTexCoord2f(0, 1); GL11.glVertex2i(x      , y+height);
		GL11.glEnd();
	}
	
	public void renderBox(float x, float y, float w, float h) {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x  , y  );
			GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x+w, y  );
			GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x+w, y+h);
			GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x  , y+h);
		GL11.glEnd();
	}
	
	/**
	 * Process this mouse event.
	 * 
	 * @param event the event
	 * @return the focused component or {@code null}
	 */
	protected final GuiListener processMouseEvent(GuiMouseEvent event, boolean includeSelf) {
		GuiListener focus = null;
		for(GuiComponent comp : children) {
			if(comp instanceof GuiListener) {
				GuiListener result = comp.processMouseEvent(event, true);
				if(result != null) {
					focus = result;
				}
			}
		}
		
		if(includeSelf && this instanceof GuiListener) {
			((GuiListener)this).onMouseEvent(event);
			if(event.getFocus()) {
				event.unsetFocus();
				return (GuiListener)this;
			}
		}
		
		return focus;
	}
	
	protected final void processKeyEvent(GuiKeyEvent event) {
		for(GuiComponent comp : children) {
			comp.processKeyEvent(event);
		}
		
		if(this instanceof GuiListener) {
			((GuiListener)this).onKeyEvent(event);
		}
	}
}
