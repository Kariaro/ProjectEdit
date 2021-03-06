package com.hardcoded.render.gui.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.files.Position;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.mc.general.world.WorldUtils;
import com.hardcoded.render.gui.GuiComponent;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiRender;
import com.hardcoded.render.gui.GuiListener.GuiEvent.*;
import com.hardcoded.render.utils.RenderUtil;

public class GuiToolList extends GuiComponent implements GuiListener {
	private Texture box;
	private Texture box_highlight;
	private Texture box_selected;
	private int index = -1;
	
	private GuiRender gui;
	
	public GuiToolList(GuiRender gui) {
		this.gui = gui;
		
		box = Texture.loadResource("/images/box.png", GL11.GL_NEAREST);
		box_highlight = Texture.loadResource("/images/box_highlight.png", GL11.GL_NEAREST);
		box_selected = Texture.loadResource("/images/box_selected.png", GL11.GL_NEAREST);
	}
	
	private Position pos1;
	private Position pos2;
	private boolean dragging;
	
	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		if(event.isConsumed()) return;
		event.requestFocus();
		
		if(event.isInside(this)) {
			event.requestFocus();
		}
		
		if(event instanceof GuiMousePress) {
			Position pos = raycastPosition();
			
			if(pos != null) {
				if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
					if(event.getAction() == GLFW.GLFW_PRESS) {
						if(!dragging) {
							pos1 = pos;
							pos2 = null;
							dragging = true;
						} else {
							pos2 = pos;
						}
					} else {
						dragging = false;
					}
				}
			}
		}
		
		if(dragging && event instanceof GuiMouseMove) {
			Position pos = raycastPosition();
			
			if(pos != null) {
				pos2 = pos;
			}
		}
		
		if(!dragging) {
			int x = getX();
			int y = getY();
			
			for(int i = 0; i < 8; i++) {
				boolean highlight = gui.isInside(x, y + i * 72, 72, 72);
				if(!LwjglWindow.isMouseCaptured()) {
					boolean mouse = event.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1);
					
					if(highlight && mouse) {
						index = i;
					}
				}
			}
		}
	}
	
	@Override
	public void onKeyEvent(GuiKeyEvent event) {
		Position pos1 = this.pos1;
		Position pos2 = this.pos2;
		if(pos1 == null || pos2 == null) return;
		
		IBlockData selected = gui.selectedBlock;
		if(selected == null) {
			selected = Blocks.STONE;
		}
		
		int x1 = pos1.getBlockX();
		int y1 = pos1.getBlockY();
		int z1 = pos1.getBlockZ();
		int x2 = pos2.getBlockX();
		int y2 = pos2.getBlockY();
		int z2 = pos2.getBlockZ();
		
		if(x2 < x1) {
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		
		if(y2 < y1) {
			int tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		
		if(z2 < z1) {
			int tmp = z1;
			z1 = z2;
			z2 = tmp;
		}
		
		World world = ProjectEdit.getInstance().getWorld();
		if(event.getKeyCode() == GLFW.GLFW_KEY_L
		&& event.getAction() == GLFW.GLFW_PRESS) {
			// Fill the area with blocks
			
			for(int i = x1; i <= x2; i++) {
				for(int j = y1; j <= y2; j++) {
					for(int k = z1; k <= z2; k++) {
						world.setBlock(selected, i, j, k);
					}
				}
			}
		}
	}
	
	private Position raycastPosition() {
		World world = ProjectEdit.getInstance().getWorld();
		Camera camera = ProjectEdit.getInstance().getCamera();
		
		Vector3f cam = camera.getPosition();
		Vector3f ray = camera.getScreenRaycast(Input.getMouseX(), Input.getMouseY());
		return WorldUtils.raycastBlock(world, cam, ray, 100);
	}
	
	@Override
	public void renderComponent() {
		int x = getX();
		int y = getY();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Camera camera = ProjectEdit.getInstance().getCamera();
		
		for(int i = 0; i < 8; i++) {
			renderBox(i, x, y + i * 72, 72, 72);
		}
		
		if(index == 0) {
			Matrix4f proj = camera.getProjectionMatrix();
			Vector3f cam = camera.getPosition();
			Vector3f ray = camera.getScreenRaycast(Input.getMouseX(), Input.getMouseY());
			
			Position pos1 = this.pos1;
			Position pos2 = this.pos2;
			
			GL11.glPushMatrix();
			GL11.glLoadMatrixf(proj.get(new float[16]));
			
			GL11.glLineWidth(2);
			GL11.glBegin(GL11.GL_LINES);
				GL11.glColor3f(1, 1, 1);
				GL11.glVertex3f(cam.x, cam.y, cam.z);
				GL11.glVertex3f(cam.x + ray.x, cam.y + ray.y, cam.z + ray.z);
			GL11.glEnd();
			
			if(pos1 != null) {
				GL11.glColor3f(1, 1, 1);
				
				float e = 0.01f;
				RenderUtil.drawWireBlock(
					pos1.getBlockX() - e,
					pos1.getBlockY() - e,
					pos1.getBlockZ() - e,
					1 + 2 * e,
					1 + 2 * e,
					1 + 2 * e
				);
			}
			
			if(pos1 != null && pos2 != null) {
				int x1 = pos1.getBlockX();
				int y1 = pos1.getBlockY();
				int z1 = pos1.getBlockZ();
				int x2 = pos2.getBlockX();
				int y2 = pos2.getBlockY();
				int z2 = pos2.getBlockZ();
				
				if(x2 < x1) {
					int tmp = x1;
					x1 = x2;
					x2 = tmp;
				}
				
				if(y2 < y1) {
					int tmp = y1;
					y1 = y2;
					y2 = tmp;
				}
				
				if(z2 < z1) {
					int tmp = z1;
					z1 = z2;
					z2 = tmp;
				}
				
				RenderUtil.drawWireBlock(
					x1,
					y1,
					z1,
					(x2 - x1 + 1),
					(y2 - y1 + 1),
					(z2 - z1 + 1)
				);
			}
			
			GL11.glPopMatrix();
		}
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public boolean renderBox(int id, int x, int y, int w, int h) {
		boolean highlight = gui.isInside(x, y, w, h);
		
		Texture tex = box;
		
		tex = highlight ? box_highlight:box;
		
		if(index == id) {
			tex = box_selected;
		}
		
		tex.bind();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2d(0, 0); GL11.glVertex2i(x  , y  );
			GL11.glTexCoord2d(1, 0); GL11.glVertex2i(x+w, y  );
			GL11.glTexCoord2d(1, 1); GL11.glVertex2i(x+w, y+h);
			GL11.glTexCoord2d(0, 1); GL11.glVertex2i(x  , y+h);
		GL11.glEnd();
		
		tex.unbind();
		
		return index == id;
	}
}
