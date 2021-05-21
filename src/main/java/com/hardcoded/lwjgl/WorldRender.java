package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.RegionChunk;
import com.hardcoded.mc.general.files.RegionChunk.SubChunk;
import com.hardcoded.mc.general.files.RegionFile;

public class WorldRender {
	private static final Logger LOGGER = LogManager.getLogger(WorldRender.class);
	
	public static final float NEAR_PLANE = 0.01f;
	public static final float FOV = 70;
	
	public static int height;
	public static int width;
	
	private final LwjglWindow parent;
	private final long window;
	
	public Camera camera;
	private WorldReader reader;
	
	public WorldRender(LwjglWindow parent, long window, int width, int height) {
		this.parent = parent;
		this.window = window;
		
		this.reader = new WorldReader();
		camera = new Camera(window);
		setViewport(width, height);
		
		try {
			init();
		} catch(Exception e) {
			LOGGER.error("Failed to load", e);
			throw e;
		}
	}
	
	public void setViewport(int width, int height) {
		WorldRender.height = height;
		WorldRender.width = width;
		
		GL11.glViewport(0, 0, width, height);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION_MATRIX);
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
	}
	
	private void init() {
		File[] files = Minecraft.getSaves();
		if(files.length > 0) {
			File save = files[0];
			
			LOGGER.info("Loading savefile '{}'", save);
			
			try {
				reader.read(new File(save, "level.dat"));
				reader.readTest(new File(save, "region/r.0.0.mca"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			LOGGER.warn("Could not find any savefiles");
		}
		

		this.displayList = GL11.glGenLists(1);
	}
	
	public int getFps() {
		return parent.getFps();
	}
	
	public void update() {
		glfwSwapBuffers(window);
		glfwPollEvents();
		camera.update();
	}
	
	private void renderCube(float x, float y, float z, float xs, float ys, float zs, int rgba) {
		float rc, gc, bc, ac;
		{
			ac = ((rgba >> 24) & 0xff) / 255.0f;
			rc = ((rgba >> 16) & 0xff) / 255.0f;
			gc = ((rgba >>  8) & 0xff) / 255.0f;
			bc = ((rgba      ) & 0xff) / 255.0f;
		}
		
		x -= 0.5f;
		y -= 0.5f;
		z -= 0.5f;
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(1, 1, 1, 0.5f);
			GL11.glVertex3f(x     , y + ys, z);
			GL11.glColor4f(rc, gc, bc, ac);
			GL11.glVertex3f(x + xs, y + ys, z);
			GL11.glVertex3f(x + xs, y     , z);
			GL11.glVertex3f(x     , y     , z);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(1, 1, 0, 0.5f);
			GL11.glVertex3f(x + xs, y + ys, z     );
			GL11.glColor4f(rc, gc, bc, ac);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x + xs, y     , z + zs);
			GL11.glVertex3f(x + xs, y     , z     );
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(1, 0, 1, 0.5f);
			GL11.glVertex3f(x     , y     , z + zs);
			GL11.glColor4f(rc, gc, bc, ac);
			GL11.glVertex3f(x + xs, y     , z + zs);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x     , y + ys, z + zs);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(0, 1, 1, 0.5f);
			GL11.glVertex3f(x, y + ys, z + zs);
			GL11.glColor4f(rc, gc, bc, ac);
			GL11.glVertex3f(x, y + ys, z     );
			GL11.glVertex3f(x, y     , z     );
			GL11.glVertex3f(x, y     , z + zs);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(1, 0, 0, 0.5f);
			GL11.glVertex3f(x     , y + ys, z     );
			GL11.glColor4f(rc, gc, bc, ac);
			GL11.glVertex3f(x     , y + ys, z + zs);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x + xs, y + ys, z     );
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(0, 0, 1, 0.5f);
			GL11.glVertex3f(x     , y, z + zs);
			GL11.glColor4f(rc, gc, bc, ac);
			GL11.glVertex3f(x     , y, z     );
			GL11.glVertex3f(x + xs, y, z     );
			GL11.glVertex3f(x + xs, y, z + zs);
		GL11.glEnd();
	}
	
	private void render_cube(float x, float y, float z, float xs, float ys, float zs, int rgba) {
		float rc, gc, bc, ac;
		{
			ac = ((rgba >> 24) & 0xff) / 255.0f;
			rc = ((rgba >> 16) & 0xff) / 255.0f;
			gc = ((rgba >>  8) & 0xff) / 255.0f;
			bc = ((rgba      ) & 0xff) / 255.0f;
		}
		
		float d =  - 0.1f;
		x -= 0.5f;
		y -= 0.5f;
		z -= 0.5f;
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(rc + d, gc, bc, ac);
			GL11.glVertex3f(x     , y + ys, z);
			GL11.glColor4f(rc, gc + d, bc + d, ac);
			GL11.glVertex3f(x + xs, y + ys, z);
			GL11.glVertex3f(x + xs, y     , z);
			GL11.glVertex3f(x     , y     , z);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(rc, gc + d, bc, ac);
			GL11.glVertex3f(x + xs, y + ys, z     );
			GL11.glColor4f(rc + d, gc, bc + d, ac);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x + xs, y     , z + zs);
			GL11.glVertex3f(x + xs, y     , z     );
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(rc, gc, bc + d, ac);
			GL11.glVertex3f(x     , y     , z + zs);
			GL11.glColor4f(rc + d, gc + d, bc, ac);
			GL11.glVertex3f(x + xs, y     , z + zs);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x     , y + ys, z + zs);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(rc + d, gc + d, bc, ac);
			GL11.glVertex3f(x, y + ys, z + zs);
			GL11.glColor4f(rc, gc, bc + d, ac);
			GL11.glVertex3f(x, y + ys, z     );
			GL11.glVertex3f(x, y     , z     );
			GL11.glVertex3f(x, y     , z + zs);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(rc + d, gc, bc + d, ac);
			GL11.glVertex3f(x     , y + ys, z     );
			GL11.glColor4f(rc, gc + d, bc, ac);
			GL11.glVertex3f(x     , y + ys, z + zs);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x + xs, y + ys, z     );
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(rc, gc + d, bc + d, ac);
			GL11.glVertex3f(x     , y, z + zs);
			GL11.glColor4f(rc + d, gc, bc, ac);
			GL11.glVertex3f(x     , y, z     );
			GL11.glVertex3f(x + xs, y, z     );
			GL11.glVertex3f(x + xs, y, z + zs);
		GL11.glEnd();
	}
	
	public static final int AIR = "minecraft:air".hashCode();
	
	public void renderSubChunk(int cx, int cy, int cz, SubChunk sub) {
		int hash = 0x30297845;
		for(int i = 0; i < 4096; i++) {
			int x = (i & 15);
			int z = (i / 16) & 15;
			int y = i / 256;
			
			int id = sub.data[i];
			if(id == AIR || id == 0) continue;
			int col = hash * id;
			
			render_cube(cx + x, cy + y, cz + z, 1, 1, 1, col);
		}
	}
	
	public void renderChunk(int x, int z, RegionChunk chunk) {
		int idx = x + z;
		int col = ((idx & 1) == 0) ? 0x666666:0xff7700;
		renderCube(x, 0, z, 16, 1, 16, col);
		
		for(int y = 0; y < 16; y++) {
			SubChunk sub = chunk.getSubChunk(y);
			if(sub == null) continue;
			renderSubChunk(x, y * 16, z, sub);
		}
	}
	
	public void renderRegionFile(int rx, int rz, RegionFile region) {
		for(int x = 0; x < 4; x++) {
			for(int z = 4; z < 8; z++) {
				if(region.hasChunk(x, z)) {
					ByteBuf buf = region.getChunkBuffer(x, z);
					
					if(buf != null) {
						renderChunk(rx + x * 16, rz + z * 16, new RegionChunk(buf));
					}
				} else {
					renderCube(rx + x * 16, 0, rz + z * 16, 16, 1, 16, 0x111111);
				}
			}
		}
	}
	
	private int displayList;
	private boolean hasList = false;
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0, 0, 0, 1);
		
		Matrix4f projectionView = camera.getProjectionMatrix(FOV, width, height);
		
		GL11.glPushMatrix();
		GL11.glLoadMatrixf(projectionView.get(new float[16]));
		GL11.glEnable(GL_DEPTH_TEST);
		GL11.glEnable(GL_CULL_FACE);
		GL11.glEnable(GL_TEXTURE_2D);
		
		for(int x = 0; x < 10; x++) {
			for(int y = 0; y < 10; y++) {
				for(int z = 0; z < 10; z++) {
					renderCube(x, y, z, 1, 1, 1, 0xff00ff);
				}
			}
		}
		renderCube(0, 0, 0, 1, 1, 1, 0xff00ff);
		
		if(!hasList) {
			hasList = true;
			GL11.glNewList(displayList, GL11.GL_COMPILE);
			int s = 0;
			for(int i = -s; i <= s; i++) {
				for(int j = -s; j <= s; j++) {
					int ix = i * 16 * 32;
					int jz = j * 16 * 32;
					
					RegionFile region = reader.tryLoad(i, j);
					if(region == null) continue;
					
					renderRegionFile(ix, jz, region);
					/*
					for(int x = 0; x < 32; x++) {
						for(int z = 0; z < 32; z++) {
							if(region.hasChunk(x, z)) {
								ByteBuf buf = region.getChunkBuffer(x, z);
								
								if(buf != null) {
									RegionChunk chunk = new RegionChunk(buf);
									System.out.println(chunk);
								}
								
								int idx = x + z;
								int col = ((idx & 1) == 0) ? 0x666666:0xff7700;
								renderCube(x * 16 + ix, 0, z * 16 + jz, 16, 1, 16, col);
							} else {
								renderCube(x * 16 + ix, 0, z * 16 + jz, 16, 1, 16, 0x111111);
							}
						}
					}
					*/
				}
			}
			GL11.glEndList();
		}
		GL11.glCallList(displayList);
		
		GL11.glPopMatrix();
		GL11.glDisable(GL_DEPTH_TEST);
		GL11.glDisable(GL_CULL_FACE);
		GL11.glColor4f(1, 1, 1, 1);
		
		Minecraft.getMinecraftPath();
	}
}
