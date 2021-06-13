package com.hardcoded.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.shader.MeshShader;
import com.hardcoded.lwjgl.shader.ShadowShader;
import com.hardcoded.lwjgl.shadow.ShadowFrameBuffer;
import com.hardcoded.lwjgl.util.MathUtils;
import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.render.gui.GuiRender;
import com.hardcoded.render.utils.RenderUtil;
import com.hardcoded.utils.VersionResourceReader;

public class LwjglRender {
	private static final Logger LOGGER = LogManager.getLogger(LwjglRender.class);
	
	public static final TextureAtlas atlas = new TextureAtlas();
	public static final float NEAR_PLANE = 0.01f;
	
	@Deprecated
	public static int height;
	@Deprecated
	public static int width;
	
	private final LwjglWindow parent;
	private final long window;
	private GuiRender gui;
	
	public Camera camera;
	private WorldRender world_render;
	public LwjglRender(LwjglWindow parent, long window, int width, int height) {
		this.parent = parent;
		this.window = window;
		
		world_render = new WorldRender(this);
		camera = new Camera(window);
		gui = new GuiRender(this);
		
		setViewport(width, height);
		
		try {
			init();
		} catch(Exception e) {
			LOGGER.error("Failed to load", e);
			throw e;
		}
	}
	
	public void setViewport(int width, int height) {
		LwjglRender.height = height;
		LwjglRender.width = width;
		
		GL11.glViewport(0, 0, width, height);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION_MATRIX);
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
	}
	
	private File version_jar;
	public World world;
	
	public ShadowShader shadowShader;
	public MeshShader meshShader;
	private void init() {
		Blocks.init();
		
		try {
			shadowShader = new ShadowShader();
			meshShader = new MeshShader();
			frameBuffer = new ShadowFrameBuffer(8192, 8192);
		} catch(Exception e) {
			throw e;
		}
		
		File[] files = Minecraft.getSaves();
		if(files.length > 0) {
			File save = files[3];
			
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
				reader.loadBlocks();
				
//				TimerUtils.begin();
//				Set<IBlockData> states = BlockDataManager.getStates();
//				int count = 0;
//				TimerUtils.beginAverage();
//				for(IBlockData state : states) {
//					BlockData dta = (BlockData)state;
//					reader.resolveState(state);
//					
//					System.out.printf("\rLoading: (%d) / (%d)", count++, states.size());
//					for(IBlockData child : dta.getChildren()) {
//						reader.resolveState(child);
//					}
//				}
//				
//				System.out.println();
//				double average = TimerUtils.endAverage();
//				
//				double time = TimerUtils.end() / 1000000.0;
//				System.out.printf("Took: %.4f ms, per item (%.4f)\n", time, time / (states.size() + 0.0));
//				System.out.printf("Average: %.8f ms\n", average / 1000000.0);
				
				atlas.compile();
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
	
	public void cleanup() {
		world_render.cleanup();
	}
	
	private int last_mvp_x;
	private int last_mvp_z = -1000000;
	private int last_mvp_y;
	private int last_mvp_scale = 1;
	public ShadowFrameBuffer frameBuffer;
	
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);
		
		Matrix4f projectionView = camera.getProjectionMatrix(width, height);
		
		{
			int mvp_scale = (int)(Math.log(camera.y - 256) - 1);
			if(mvp_scale < 1) mvp_scale = 1;
			boolean updateShadowMap = false;
			int cmx = Math.floorDiv((int)camera.x, 16) * 16;
			int cmy = Math.floorDiv((int)camera.y, 16) * 16;
			int cmz = Math.floorDiv((int)camera.z, 16) * 16;
			if(last_mvp_x != cmx || last_mvp_z != cmz || last_mvp_y != cmy || last_mvp_scale != mvp_scale) {
				last_mvp_scale = mvp_scale;
				last_mvp_x = cmx;
				last_mvp_y = cmy;
				last_mvp_z = cmz;
				updateShadowMap = true;
			}
			
			Matrix4f mvpMatrix = MathUtils.getOrthoProjectionMatrix(512 * mvp_scale, 512 * mvp_scale, 400);
			
			{
				float step = (System.currentTimeMillis() % 72000L) / 4000.0f;
				step = 45;
				step = (int)(step);
				
				int x = last_mvp_x;
				int z = last_mvp_z;
				
				float angle = (float)Math.toRadians(step);
				mvpMatrix.rotateLocalX(-(float)Math.PI / 2.0f);
				mvpMatrix.rotateZ(0.8f);
				mvpMatrix.rotateY(angle);
				mvpMatrix.translate(-x, -100, -z);
			}
			
			int radius = 16;
			
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0);
			
			if(updateShadowMap) {
				shadowShader.bind();
				shadowShader.setMvpMatrix(mvpMatrix);
				frameBuffer.bindFrameBuffer();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				
				world_render.renderWorld(world, camera, projectionView, radius);
				shadowShader.unbind();
				frameBuffer.unbindFrameBuffer();
			}
			
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffer.getShadowMap());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.getTextureId());
			
			{
				meshShader.bind();
				meshShader.setShadowMapSpace(MathUtils.getShadowSpaceMatrix(mvpMatrix));
				meshShader.setProjectionView(projectionView);
				world_render.renderWorld(world, camera, projectionView, radius);
				meshShader.unbind();
			}
			
			atlas.unbind();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_O)) {
				camera.x = 135;
				camera.y = 331;
				camera.z = 67;
				camera.ry = 90;
				camera.rx = 180;
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_P)) {
				camera.x = 1648;
				camera.y = 68.26f;
				camera.z = -2434;
				camera.rx = 180;
				camera.ry = 10;
			}
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			GL11.glPushMatrix();
			GL11.glLoadMatrixf(projectionView.get(new float[16]));
			drawLoadedChunks();
//			drawFrustumChunks(projectionView, radius);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glLoadMatrixf(projectionView.get(new float[16]));
//				RenderUtil.drawFrustum(projViewTest, campos, width, height, 20, 100);
				
				{
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.getTextureId());
					GL11.glColor4d(1, 1, 1, 1);
					GL11.glBegin(GL11.GL_TRIANGLE_FAN);
						GL11.glTexCoord2d(0, 0); GL11.glVertex3d(  0, 256, 0);
						GL11.glTexCoord2d(1, 0); GL11.glVertex3d(256, 256, 0);
						GL11.glTexCoord2d(1, 1); GL11.glVertex3d(256, 256, 256);
						GL11.glTexCoord2d(0, 1); GL11.glVertex3d(  0, 256, 256);
					GL11.glEnd();
				}
				
//				{ 
//					GL13.glActiveTexture(GL13.GL_TEXTURE0);
//					GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffer.getShadowMap());
//					GL11.glColor4d(1, 1, 1, 1);
//					GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//						GL11.glTexCoord2d(0, 0); GL11.glVertex3d(  0, 300, 0);
//						GL11.glTexCoord2d(1, 0); GL11.glVertex3d(256, 300, 0);
//						GL11.glTexCoord2d(1, 1); GL11.glVertex3d(256, 300, 256);
//						GL11.glTexCoord2d(0, 1); GL11.glVertex3d(  0, 300, 256);
//					GL11.glEnd();
//				}
			GL11.glPopMatrix();
		}
		
		GL11.glDisable(GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		gui.render();
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	private int displayList;
	private int last_size = -1;
	void drawLoadedChunks() {
		List<IRegion> copy = new ArrayList<>(world.getChunkProvider().getRegions());
		int size = 0;
		for(IRegion region : copy) {
			size += (region instanceof Region) ? 1:0;
		}
		
		if(size == last_size) {
			GL11.glCallList(displayList);
			return;
		}
		last_size = size;
		
		if(displayList != 0) {
			GL11.glDeleteLists(displayList, 0);
		}
		
		displayList = GL11.glGenLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		
		float yo = -0.1f;
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor3f(0.7f, 0.7f, 0.7f);
		for(IRegion region : copy) {
			if(!region.isLoaded()) continue;
			
			int rx = region.getX() * 512;
			int rz = region.getZ() * 512;
			
			final int len = 32 * 32;
			for(int i = 0; i < len; i++) {
				int x = i & 31;
				int z = i / 32;
				
				if(((x ^ z) & 1) != 0) {
					if(region.hasChunk(x, z)) {
						int gx = rx + x * 16;
						int gz = rz + z * 16;
						
						GL11.glVertex3f(gx     , yo, gz     );
						GL11.glVertex3f(gx     , yo, gz + 16);
						GL11.glVertex3f(gx + 16, yo, gz + 16);
						
						GL11.glVertex3f(gx     , yo, gz     );
						GL11.glVertex3f(gx + 16, yo, gz + 16);
						GL11.glVertex3f(gx + 16, yo, gz     );
					}
				}
			}
		}
		
		GL11.glColor3f(0.5f, 0.5f, 0.5f);
		for(IRegion region : copy) {
			if(!region.isLoaded()) continue;
			
			int rx = region.getX() * 512;
			int rz = region.getZ() * 512;
			
			final int len = 32 * 32;
			for(int i = 0; i < len; i++) {
				int x = i & 31;
				int z = i / 32;
				
				if(((x ^ z) & 1) == 0) {
					if(region.hasChunk(x, z)) {
						int gx = rx + x * 16;
						int gz = rz + z * 16;
						
						GL11.glVertex3f(gx     , yo, gz     );
						GL11.glVertex3f(gx     , yo, gz + 16);
						GL11.glVertex3f(gx + 16, yo, gz + 16);
						
						GL11.glVertex3f(gx     , yo, gz     );
						GL11.glVertex3f(gx + 16, yo, gz + 16);
						GL11.glVertex3f(gx + 16, yo, gz     );
					}
				}
			}
		}
		GL11.glEnd();
		
		GL11.glEndList();
		GL11.glCallList(displayList);
	}
	
	void drawFrustumChunks(Matrix4f projectionView, int radius) {
		int x = Math.floorDiv((int)camera.x, 16);
		int z = Math.floorDiv((int)camera.z, 16);
		final int xs = x - radius;
		final int xe = x + radius;
		final int zs = z - radius;
		final int ze = z + radius;
		
		FrustumIntersection intersect = new FrustumIntersection(projectionView);
		
		GL11.glLineWidth(1.0f);
		for(int i = xs; i <= xe; i++) {
			for(int j = zs; j <= ze; j++) {
				IChunk chunk = world.getChunk(i, j);
				
				if(chunk.isLoaded()) {
					GL11.glColor4f(1, 1, 1, 1);
				} else {
					GL11.glColor4f(1, 0, 0, 1);
				}
				
				for(int k = 0; k < 16; k++) {
					if(intersect.testAab(new Vector3f(i * 16, k * 16, j * 16), new Vector3f((i + 1) * 16, (k + 1) * 16, (j + 1) * 16))) {
						if(!chunk.isLoaded()) {
							RenderUtil.drawWireBlock(i * 16, 0, j * 16, 16, 16, 16);
							break;
						}
						
						if(!chunk.getSection(k).isLoaded()) {
							continue;
						}
						
						RenderUtil.drawWireBlock(i * 16, k * 16, j * 16, 16, 16, 16);
					}
				}
			}
		}
	}
}
