package com.hardcoded.render.gui;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.mc.general.files.Position;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.mc.general.world.WorldUtils;
import com.hardcoded.render.LwjglRender;
import com.hardcoded.render.utils.RenderUtil;

public class GuiToolList {
	private Texture box;
	private Texture box_highlight;
	private Texture box_selected;
	private int index = -1;
	
	private final GuiRender gui;
	
	public GuiToolList(GuiRender gui) {
		this.gui = gui;
		
		box = Texture.loadResource("/images/box.png", GL11.GL_NEAREST);
		box_highlight = Texture.loadResource("/images/box_highlight.png", GL11.GL_NEAREST);
		box_selected = Texture.loadResource("/images/box_selected.png", GL11.GL_NEAREST);
	}
	
	public void render() {
		Camera camera = gui.render.camera;
		World world = gui.render.world;
		
		for(int i = 0; i < 8; i++) {
			renderBox(i, 0, i * 72, 72, 72);
		}
		
		if(index == 0) {
			float width = LwjglRender.width;
			float height = LwjglRender.height;
			
			Matrix4f proj = camera.getProjectionMatrix(width, height);
			Vector3f cam = camera.getPosition();
			Vector3f ray = RenderUtil.getRay(proj, (int)width, (int)height, Input.getMouseX(), Input.getMouseY())
					.sub(cam);
			
			Position pos = WorldUtils.raycastBlock(world, cam, ray, 100);
			
			GL11.glPushMatrix();
			GL11.glLoadMatrixf(proj.get(new float[16]));
			
			GL11.glLineWidth(2);
			GL11.glBegin(GL11.GL_LINES);
				GL11.glColor3f(1, 1, 1);
				GL11.glVertex3f(cam.x, cam.y, cam.z);
				GL11.glVertex3f(cam.x + ray.x, cam.y + ray.y, cam.z + ray.z);
			GL11.glEnd();
			
			if(pos != null) {
				GL11.glColor3f(1, 1, 1);
				
				float e = 0.01f;
				RenderUtil.drawWireBlock(
					pos.getBlockX() - e,
					pos.getBlockY() - e,
					pos.getBlockZ() - e,
					1 + 2 * e,
					1 + 2 * e,
					1 + 2 * e
				);
			}
			
			GL11.glPopMatrix();
		}
	}
	
	public boolean renderBox(int id, int x, int y, int w, int h) {
		boolean highlight = gui.isInside(x, y, w, h);
		
		Texture tex = box;
		if(!LwjglWindow.isMouseCaptured()) {
			boolean mouse = Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1);
			
			if(highlight && mouse) {
				index = id;
			}
			
			tex = highlight ? box_highlight:box;
		}
		
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
