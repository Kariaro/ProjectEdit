package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.*;
import com.hardcoded.utils.FastModelJsonLoader;
import com.hardcoded.utils.TimerUtils;
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
			File save = files[1];
			
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
				
				TimerUtils.begin();
				Set<IBlockData> states = BlockDataManager.getStates();
				int count = 0;
				TimerUtils.beginAverage();
				for(IBlockData state : states) {
					BlockData dta = (BlockData)state;
					reader.resolveState(state);
					
					System.out.printf("\rLoading: (%d) / (%d)", count++, states.size());
					for(IBlockData child : dta.getChildren()) {
						reader.resolveState(child);
					}
				}
				
				System.out.println();
				double average = TimerUtils.endAverage();
				
				double time = TimerUtils.end() / 1000000.0;
				System.out.printf("Took: %.4f ms, per item (%.4f)\n", time, time / (states.size() + 0.0));
				System.out.printf("Average: %.8f ms\n", average / 1000000.0);
				
				FastModelJsonLoader.atlas.compile();
			} catch(Exception e) {
				LOGGER.trace(e);
				e.printStackTrace();
			}
		} else {
			LOGGER.warn("Could not find any savefiles");
		}
	}
	
	public int getFps() {
		return parent.getFps();
	}
	
	public void update() {
		glfwSwapBuffers(window);
		glfwPollEvents();
		camera.update();
	}
	
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);
		
		Matrix4f projectionView = camera.getProjectionMatrix(FOV, width, height);
		
		GL11.glPushMatrix();
		GL11.glLoadMatrixf(projectionView.get(new float[16]));
		GL11.glEnable(GL_DEPTH_TEST);
		GL11.glEnable(GL_CULL_FACE);
		GL11.glEnable(GL_TEXTURE_2D);
		
		for(int i = 0; i < 256; i++) {
			ChunkRender.renderBlock(i & 15, 0, i / 16, new Vector4f(1, 0, 0, 0));
		}
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0);
		
		chunk_render.renderWorld(world, camera, 16);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL_CULL_FACE);
		
		FastModelJsonLoader.atlas.bind();
		{
			GL11.glColor3d(1, 1, 1);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex3d(  0, 256, 0);
			
			GL11.glTexCoord2d(1, 0);
			GL11.glVertex3d(256, 256, 0);
			
			GL11.glTexCoord2d(1, 1);
			GL11.glVertex3d(256, 256, 256);
			
			GL11.glTexCoord2d(0, 1);
			GL11.glVertex3d(  0, 256, 256);
			GL11.glEnd();
		}
		FastModelJsonLoader.atlas.unbind();
		
		GL11.glPopMatrix();
		GL11.glDisable(GL_DEPTH_TEST);
	}
}
