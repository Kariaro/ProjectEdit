package com.hardcoded.render.gui.components;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.lwjgl.input.InputMask;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.world.BlockDataManager;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.render.gui.GuiComponent;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.*;
import com.hardcoded.render.gui.GuiRender;

public class GuiBlockMenu extends GuiComponent implements GuiListener {
	private List<GuiBlockListItem> menuItem;
	private GuiRender gui;
	
	public GuiBlockMenu(GuiRender gui) {
		this.gui = gui;
		
		List<IBlockData> blocks = new ArrayList<>(BlockDataManager.getStates());
		blocks.sort((s1, s2) -> {
			return s1.getName().compareTo(s2.getName());
		});
		
		menuItem = new ArrayList<>();
		for(int i = 0, len = blocks.size(); i < len; i++) {
			GuiBlockListItem comp = new GuiBlockListItem().setBlock(blocks.get(i));
			menuItem.add(comp);
		}
		
		setSize(112, 120);
	}
	
	private float scroll_amount;
	private boolean has_scroll_mouse;
	private boolean has_resize_mouse;
	private boolean mouse_hover_scroll;
	
	public int getBlockScreenWidth() {
		return getWidth() - 48;
	}
	
	public int getBlockScreenHeight() {
		return getHeight() - 56;
	}
	
	private int getBlockMenuHeight() {
		int xb = getBlockScreenWidth() / 64;
		int rows = (menuItem.size() + xb - 1) / xb;
		return rows;
	}
	
	private float getScroll() {
		return scroll_amount / (getHeight() - 48.0f);
	}
	
	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		int width = getWidth();
		int height = getHeight();
		int x = getX();
		int y = getY();
		
		if(event.isInside(this)) {
			event.consume();
			
			if(event instanceof GuiMouseScroll) {
				scroll_amount -= event.getScrollAmount() * 4;
			}
		} else {
			
		}
		
		if(event.isInside(x + width - 32, y + 8 + scroll_amount, 24, 32)) {
			mouse_hover_scroll = true;
		} else {
			mouse_hover_scroll = false;
		}
		
		if(event.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			if(has_resize_mouse || has_scroll_mouse) {
				
			} else {
				if(mouse_hover_scroll) {
					has_scroll_mouse = true;
				}
				
				if(event.isInside(x + width - 8, y + height - 8, 8, 8)) {
					has_resize_mouse = true;
				}
			}
		} else {
			has_scroll_mouse = false;
			has_resize_mouse = false;
		}
		
		if(has_resize_mouse) {
			float mx = event.getX() - x + 4;
			float my = event.getY() - y + 4;
			
			width = (int)mx;
			height = (int)my;
			if(width < 112) {
				width = 112;
			}
			
			if(height < 120) {
				height = 120;
			}
			
			setSize(width, height);
		} else if(has_scroll_mouse) {
			scroll_amount = event.getY() - 24 - y;
		}
		
		if(scroll_amount > height - 48) scroll_amount = height - 48;
		if(scroll_amount < 0) scroll_amount = 0;

		boolean mouse_inside_block_screen = event.isInside(getX() + 8, getY() + 48, getBlockScreenWidth(), getBlockScreenHeight());
		
		if(mouse_inside_block_screen) {
			if(!(has_resize_mouse || has_scroll_mouse)) {
				if(event instanceof GuiMouseMove
				|| event instanceof GuiMouseScroll) {
					if(mouse_inside_block_screen) {
						callOnScreenItems((item, idx, c_xp, c_yp) -> {
							item.hover = false;
							if(event.isInside(c_xp, c_yp, 64, 64)) {
								item.hover = true;
							}
						});
					}
				}
				
				if(event instanceof GuiMousePress
				&& event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1
				&& event.getAction() == GLFW.GLFW_PRESS) {
					for(GuiBlockListItem item : menuItem) {
						item.selected = false;
						item.hover = false;
					}
					
					callOnScreenItems((item, idx, c_xp, c_yp) -> {
						item.selected = false;
						if(event.isInside(c_xp, c_yp, 64, 64)) {
							item.selected = true;
							gui.selectedBlock = item.getBlock();
						}
					});
				}
			}
		}
	}

	@Override
	public void onKeyEvent(GuiKeyEvent event) {
		
	}
	
	@FunctionalInterface
	private interface IItemConsumer {
		void apply(GuiBlockListItem item, int idx, float x, float y);
	}
	
	private void callOnScreenItems(IItemConsumer consumer) {
		int height = getHeight();
		int x = getX();
		int y = getY();
		
		int menuItems = menuItem.size();
		
		int block_screen_width = getBlockScreenWidth();
		int block_screen_height = getBlockScreenHeight();
		int xt = Math.max(1, block_screen_width / 64);
		int yt = Math.max(1, block_screen_height / 64);
		int rows = getBlockMenuHeight();
		
		float scroll = getScroll();
		float ys = scroll * rows - yt;
		
		int is = Math.max((int)ys - 1, 0);
		
		int pixel_height = rows * 64 - block_screen_height;
		float scroll_px = scroll * pixel_height;
		
		for(int i = is;; i++) {
			float p_yp = y + 48 + i * 64 - scroll_px;
			if(p_yp > y + height - 8) break;
			if(p_yp < y - 24) continue;
			
			for(int j = 0; j < xt; j++) {
				float p_xp = x + 8 + j * 64;
				
				int idx = i * xt + j;
				if(idx >= menuItems) return;
				
				consumer.apply(menuItem.get(idx), idx, p_xp, p_yp);
			}
		}
	}

	@Override
	public void renderComponent() {
		int width = getWidth();
		int height = getHeight();
		int x = getX();
		int y = getY();

		if(!LwjglWindow.isMouseCaptured()) {
			InputMask.addEventMask(x, y, width, height, this);
		}
		
//		testDraw();
		
		int block_screen_width = getBlockScreenWidth();
		int block_screen_height = getBlockScreenHeight();
		
		GL11.glColor4f(0.1f, 0.1f, 0.1f, 1);
		renderBox(x, y, width, height);
		
		GL11.glColor4f(0.3f, 0.3f, 0.3f, 1);
		renderBox(x + 8, y + 8, width - 48, 32);
		renderBox(x + width - 32, y + 8, 24, height - 16);
		
		GL11.glColor4f(0.6f, 0.6f, 0.6f, 1);
		if(has_scroll_mouse) {
			GL11.glColor4f(0.8f, 0.8f, 0.8f, 1);
		} else if(mouse_hover_scroll) {
			GL11.glColor4f(0.7f, 0.7f, 0.7f, 1);
		}
		
		renderBox(x + width - 32, y + 8 + scroll_amount, 24, 32);
		
		if(has_resize_mouse) {
			GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);
		} else {
			GL11.glColor4f(0.7f, 0.7f, 0.7f, 1);
		}
		renderBox(x + width - 8, y + height - 8, 8, 8);
		
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
		GL11.glStencilMask(0xFF);
		GL11.glColor4f(0.7f, 0.7f, 0.7f, 1);
		renderBox(x + 8, y + 48, block_screen_width, block_screen_height);
		
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		GL11.glStencilMask(0xFF);
		
		{
			int rows = getBlockMenuHeight();
			int pixel_height = rows * 64 - block_screen_height;
			float scroll_px = (getScroll() * pixel_height) % 64;
			
			int wi = getBlockScreenWidth() / 64;
			int he = (getBlockScreenHeight() / 64) + 2;
			
			Texture tex = ProjectEdit.getInstance().getTextureManager().getGuiIcons().blockmenu_box;
			tex.bind();
			renderBox((int)x + 8, (int)y + 48 - (int)scroll_px, 64 * wi, 64 * he, wi, he);
			tex.unbind();
		}
		
		callOnScreenItems((comp, idx, c_xp, c_yp) -> {
			comp.setLocation((int)c_xp, (int)c_yp);
			comp.renderComponent();
		});
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
	
	@Override
	public GuiBlockMenu setSize(int width, int height) {
		super.setSize(width, height);
		return this;
	}
	
	private void renderBox(int x, int y, int w, int h, int tx, int ty) {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2i( 0,  0); GL11.glVertex2i(x  , y  );
			GL11.glTexCoord2i(tx,  0); GL11.glVertex2i(x+w, y  );
			GL11.glTexCoord2i(tx, ty); GL11.glVertex2i(x+w, y+h);
			GL11.glTexCoord2i( 0, ty); GL11.glVertex2i(x  , y+h);
		GL11.glEnd();
	}
	
	private Texture test;
	void testDraw() {
		if(test == null) {
			test = Texture.loadResource("/gui/border_1.png", GL11.GL_NEAREST);
		}
		
		if(test == null) return;
		GL11.glColor4f(1, 1, 1, 1);
		test.bind();
		
		int scale = 2;
		int bx = 300;
		int by = 300;
		int bw = 10;
		int bh = 10;
		int bc = 3 * scale;
		
		bw *= scale;
		bh *= scale;
		
		int x0 = bx;
		int x1 = bx + bw + bc;
		int y0 = by;
		int y1 = by + bh + bc;
		
		// Corner
		blit(x0, y0, bc, bc, 1, 1, 3, 3);
		blit(x1, y0, bc, bc, 3, 1, 3, 3);
		blit(x0, y1, bc, bc, 1, 3, 3, 3);
		blit(x1, y1, bc, bc, 3, 3, 3, 3);
		
		// Edges
		blit(x0 + bc, y0, bw, bc, 3, 1, 1, 3);
		blit(x0 + bc, y1, bw, bc, 3, 3, 1, 3);
		blit(x0, y0 + bc, bc, bh, 1, 3, 3, 1);
		blit(x1, y0 + bc, bc, bh, 3, 3, 3, 1);
		
		// Middle
		blit(x0 + bc, y0 + bc, bw, bh, 3, 3, 1, 1);
		
//		int br = 4 * scale;
//		int x0 = bx;
//		int x1 = bx + bw + br;
//		int y0 = by;
//		int y1 = by + bh + bc;
//		// Corner
//		blit(x0, y0, br, bc, 0, 0, 4, 3);
//		blit(x1, y0, br, bc, 3, 0, 4, 3);
//		blit(x0, y1, br, bc, 0, 4, 4, 3);
//		blit(x1, y1, br, bc, 3, 4, 4, 3);
//		
//		// Edges
//		blit(x0 + br, y0, bw, bc, 3, 0, 1, 3);
//		blit(x0 + br, y1, bw, bc, 3, 4, 1, 3);
//		blit(x0 + scale, y0 + bc, bc, bh, 0, 3, 3, 1);
//		blit(x1, y0 + bc, bc, bh, 4, 3, 3, 1);
//		
//		// Middle
//		blit(x0 + br, y0 + bc, bw, bh, 3, 3, 1, 1);
		
		
		test.unbind();
	}
	
	private void blit(int x, int y, int w, int h, int tx, int ty, int tw, int th) {
		// Texture width is 7
		
		float tx0 = (tx     ) / 7.0f;
		float tx1 = (tx + tw) / 7.0f;
		float ty0 = (ty     ) / 7.0f;
		float ty1 = (ty + th) / 7.0f;
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2f(tx0, ty0); GL11.glVertex2i(x  , y  );
			GL11.glTexCoord2f(tx1, ty0); GL11.glVertex2i(x+w, y  );
			GL11.glTexCoord2f(tx1, ty1); GL11.glVertex2i(x+w, y+h);
			GL11.glTexCoord2f(tx0, ty1); GL11.glVertex2i(x  , y+h);
		GL11.glEnd();
	}
}
