package com.hardcoded.render.gui.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.icon.GuiIcons;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.input.InputMask;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.files.Position;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.mc.general.world.WorldUtils;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.*;
import com.hardcoded.render.util.RenderDrawingUtil;
import com.hardcoded.render.gui.GuiRender;

public class GuiToolSelection extends GuiTool implements GuiListener {
	private final GuiIcons icons;
	
	public GuiToolSelection(GuiRender gui, int index) {
		super(gui, index);
		this.icons = ProjectEdit.getInstance().getTextureManager().getGuiIcons();
	}
	
	private Position pos1;
	private Position pos2;
	
	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		if(event instanceof GuiMousePress) {
			Position pos = raycastPosition();
			
			if(pos != null) {
				if(event.getButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
					if(event.getAction() == GLFW.GLFW_PRESS) {
						pos1 = pos;
						pos2 = null;
					}
				}
			}
		}
		
		if(event instanceof GuiMouseDrag) {
			Position pos = raycastPosition();
			
			if(pos != null) {
				pos2 = pos;
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
			selected = Blocks.AIR;
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
	protected void renderGui() {
		icons.tool_selection.bind();
		renderBox();
		icons.tool_selection.unbind();
	}
	
	@Override
	protected void renderWorld() {
		if(!selected) return;
		InputMask.requestFocus(this);
		InputMask.addEventMaskLast(0, 0, LwjglWindow.getWidth(), LwjglWindow.getHeight(), null, this);
		
		Camera camera = ProjectEdit.getInstance().getCamera();
		Matrix4f proj = camera.getProjectionAndTranslationMatrix();
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
			RenderDrawingUtil.drawWireBlock(
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
			
			RenderDrawingUtil.drawWireBlock(
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
}
