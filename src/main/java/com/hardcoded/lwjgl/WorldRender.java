package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.*;
import com.hardcoded.utils.ModelJsonLoader;
import com.hardcoded.utils.VersionResourceReader;

public class WorldRender {
	private static final Logger LOGGER = LogManager.getLogger(WorldRender.class);
	
	public static final float NEAR_PLANE = 0.01f;
	public static final float FOV = 70;
	
	public static int height;
	public static int width;
	
	private final LwjglWindow parent;
	private final long window;
	
	public Camera camera;
	private ChunkRender chunk_render;
	public WorldRender(LwjglWindow parent, long window, int width, int height) {
		this.parent = parent;
		this.window = window;
		
		chunk_render = new ChunkRender();
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
	
	private World world;
	private File version_jar;
	private void init() {
		Blocks.init();
		
		File[] files = Minecraft.getSaves();
		if(files.length > 0) {
			File save = files[0];
			
			LOGGER.info("Loading savefile '{}'", save);
			
			try {
				world = new World(save);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			version_jar = new File(Minecraft.getVersionFolder(world.getVersion()), world.getVersion() + ".jar");
			LOGGER.info("Version file: '{}'", version_jar);
			
			try {
				VersionResourceReader reader = new VersionResourceReader(version_jar);
				
				for(IBlockState state : BlockStates.getStates()) {
					System.out.println("################################################################");
					JSONObject json = reader.resolveState(state);
					LOGGER.info("State: {}, {}", state, state.getName());
					LOGGER.info("Entry: {}", json);
					LOGGER.info("");
					
					if(json != null) {
						((BlockState)state).model = ModelJsonLoader.createModel(reader, json);
					}
				}
			} catch(Exception e) {
				LOGGER.trace(e);
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
	
//	private void renderCube(float x, float y, float z, float xs, float ys, float zs, int rgba) {
//		float rc, gc, bc, ac;
//		{
//			ac = ((rgba >> 24) & 0xff) / 255.0f;
//			rc = ((rgba >> 16) & 0xff) / 255.0f;
//			gc = ((rgba >>  8) & 0xff) / 255.0f;
//			bc = ((rgba      ) & 0xff) / 255.0f;
//		}
//		
//		x -= 0.5f;
//		y -= 0.5f;
//		z -= 0.5f;
//		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//			GL11.glColor4f(1, 1, 1, 0.5f);
//			GL11.glVertex3f(x     , y + ys, z);
//			GL11.glColor4f(rc, gc, bc, ac);
//			GL11.glVertex3f(x + xs, y + ys, z);
//			GL11.glVertex3f(x + xs, y     , z);
//			GL11.glVertex3f(x     , y     , z);
//		GL11.glEnd();
//		
//		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//			GL11.glColor4f(1, 1, 0, 0.5f);
//			GL11.glVertex3f(x + xs, y + ys, z     );
//			GL11.glColor4f(rc, gc, bc, ac);
//			GL11.glVertex3f(x + xs, y + ys, z + zs);
//			GL11.glVertex3f(x + xs, y     , z + zs);
//			GL11.glVertex3f(x + xs, y     , z     );
//		GL11.glEnd();
//		
//		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//			GL11.glColor4f(1, 0, 1, 0.5f);
//			GL11.glVertex3f(x     , y     , z + zs);
//			GL11.glColor4f(rc, gc, bc, ac);
//			GL11.glVertex3f(x + xs, y     , z + zs);
//			GL11.glVertex3f(x + xs, y + ys, z + zs);
//			GL11.glVertex3f(x     , y + ys, z + zs);
//		GL11.glEnd();
//		
//		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//			GL11.glColor4f(0, 1, 1, 0.5f);
//			GL11.glVertex3f(x, y + ys, z + zs);
//			GL11.glColor4f(rc, gc, bc, ac);
//			GL11.glVertex3f(x, y + ys, z     );
//			GL11.glVertex3f(x, y     , z     );
//			GL11.glVertex3f(x, y     , z + zs);
//		GL11.glEnd();
//		
//		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//			GL11.glColor4f(1, 0, 0, 0.5f);
//			GL11.glVertex3f(x     , y + ys, z     );
//			GL11.glColor4f(rc, gc, bc, ac);
//			GL11.glVertex3f(x     , y + ys, z + zs);
//			GL11.glVertex3f(x + xs, y + ys, z + zs);
//			GL11.glVertex3f(x + xs, y + ys, z     );
//		GL11.glEnd();
//		
//		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//			GL11.glColor4f(0, 0, 1, 0.5f);
//			GL11.glVertex3f(x     , y, z + zs);
//			GL11.glColor4f(rc, gc, bc, ac);
//			GL11.glVertex3f(x     , y, z     );
//			GL11.glVertex3f(x + xs, y, z     );
//			GL11.glVertex3f(x + xs, y, z + zs);
//		GL11.glEnd();
//	}
	
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
		
//		for(int x = 0; x < 10; x++) {
//			for(int y = 0; y < 10; y++) {
//				for(int z = 0; z < 10; z++) {
//					renderCube(x, y, z, 1, 1, 1, 0xff00ff);
//				}
//			}
//		}
		
		if(!hasList) {
			hasList = true;
			GL11.glNewList(displayList, GL11.GL_COMPILE);
			int s = 2;
//			
//			for(int i = 0; i < 256; i++) {
//				world.setBlock(Blocks.DIRT, 0, 0, i);
//				world.setBlock(Blocks.AIR, 0, 1, i);
//			}
			chunk_render.renderWorld(world, 0, 0, s);
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
