package com.hardcoded.render;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglSettings;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.icon.TextureManager;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.shader.MeshShader;
import com.hardcoded.lwjgl.shader.ShadowShader;
import com.hardcoded.lwjgl.shadow.ShadowFrameBuffer;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.files.IChunk;
import com.hardcoded.mc.general.files.IRegion;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.render.gui.GuiRender;
import com.hardcoded.render.menubar.MenuBar;
import com.hardcoded.render.utils.RenderUtil;
import com.hardcoded.utils.*;

public class LwjglRender {
	private static final Logger LOGGER = LogManager.getLogger(LwjglRender.class);
	
	private final long window;
	private MenuBar menuBar;
	private GuiRender gui;
	
	// Initialized from ProjectEdit.getInstance().getWorld()
	public World world;
	// Initialized from ProjectEdit.getInstance().getCamera()
	public Camera camera;
	
	private WorldRender world_render;
	public LwjglRender(long window, int width, int height) {
		this.window = window;
		
		camera = ProjectEdit.getInstance().getCamera();
		gui = new GuiRender();
		world_render = new WorldRender(this);
		menuBar = new MenuBar();
		
		setViewport(width, height);
		
		try {
			init();
		} catch(Exception e) {
			LOGGER.error("Failed to load", e);
			throw e;
		}
	}
	
	public void setViewport(int width, int height) {
		GL11.glViewport(0, 0, width, height);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION_MATRIX);
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
	}
	
	private File version_jar;
	private VersionResourceReader reader;
	private TextureManager textureManager;
	private TextureAtlas atlas;
	
	public ShadowShader shadowShader;
	public MeshShader meshShader;
	private void init() {
		Blocks.init();
		
		try {
			shadowShader = new ShadowShader();
			meshShader = new MeshShader();
			frameBuffer = new ShadowFrameBuffer(8192, 8192);
			textureManager = ProjectEdit.getInstance().getTextureManager();
			textureManager.init();
			
			atlas = textureManager.getBlockAtlas();
		} catch(Exception e) {
			throw e;
		}
		
		try {
			GL43.glDebugMessageCallback(new GLDebugMessageCallback() {
				@Override
				public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
					Level level = getSeverityLevel(severity);
					String msg = getMessage(message, length);
					
					LOGGER.log(level, "[DEBUG MESSAGE CALLBACK]: {}", msg);
				}
				
				private String getMessage(long pointer, int length) {
					ByteBuffer buffer = MemoryUtil.memByteBuffer(pointer, length);
					return new String(buffer.array());
				}
				
				private Level getSeverityLevel(int severity) {
					switch(severity) {
						case GL43.GL_DEBUG_SEVERITY_HIGH:
							return Level.ERROR;
						case GL43.GL_DEBUG_SEVERITY_MEDIUM:
							return Level.WARN;
						case GL43.GL_DEBUG_SEVERITY_LOW:
							return Level.WARN;
						default:
						case GL43.GL_DEBUG_SEVERITY_NOTIFICATION:
							return Level.DEBUG;
					}
				}
			}, GL43.GL_DEBUG_CALLBACK_USER_PARAM | GL43.GL_DEBUG_CALLBACK_FUNCTION);
		} catch(Exception e) {
			throw e;
		}
		
		try {
			menuBar.reload();
		} catch(Exception e) {
			LOGGER.trace(e);
			e.printStackTrace();
		}
	}
	
	public void reloadTextures() {
		TimerUtils.begin();
		
		// Unload rendered chunks
		world_render.unloadCache();
		// Unload model cache
		FastModelJsonLoader.unloadCache();
		// Unload all textures
		atlas.unload();
		
		// Reload models
		reader.loadBlocks();
		
		// Add debug texture
		try {
			debug_id = atlas.addTexture("projectedit:debug_faces", ImageIO.read(LwjglRender.class.getResourceAsStream("/images/debug_faces.png")));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Recompile textures
		atlas.reload();
		
		// Icon generation
		textureManager.getIconGenerator().loadIcons();
		
		long nanos = TimerUtils.end();
		
		gui.init();
		
		LOGGER.info("Took: {} ms to reload textures", nanos / 1000000.0);
		
		GL11.glViewport(0, 0, LwjglWindow.getWidth(), LwjglWindow.getHeight());
	}
	
	public void update() {
		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();
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
	
	protected Vector3f camera_pos;
	protected Matrix4f camera_view;
	private int debug_id;
	
	private World last_world;
	public void render() {
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		Matrix4f projectionView = camera.getProjectionMatrix();
		
		// Get the current loaded world
		World world = ProjectEdit.getInstance().getWorld();
		
		if(last_world != world) {
			last_world = world;
			world_render.unloadCache();
			
			if(world != null) {
				version_jar = new File(Minecraft.getVersionFolder(world.getVersion()), world.getVersion() + ".jar");
				LOGGER.info("Version file: '{}'", version_jar);
				
				try {
					reader = new VersionResourceReader(version_jar);
				} catch(IOException e) {
					e.printStackTrace();
				}
				
				reloadTextures();
				
				if(has_regions) {
					GL11.glDeleteLists(displayList, 1);
					has_regions = false;
				}
			}
		}
		
		if(Input.isKeyDown(GLFW.GLFW_KEY_U) || camera_pos == null) {
			camera_pos = camera.getPosition();
			camera_view = camera.getProjectionMatrix();
		}
		
		if(Input.isControlDown()) {
			if(Input.pollKey(GLFW.GLFW_KEY_F5)) {
				gui.init();
			}
			
			if(Input.pollKey(GLFW.GLFW_KEY_T)) {
				reloadTextures();
				GL11.glViewport(0, 0, LwjglWindow.getWidth(), LwjglWindow.getHeight());
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_1)) {
				camera.x = 135;
				camera.y = 331;
				camera.z = 67;
				camera.ry = 90;
				camera.rx = 180;
			}
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_2)) {
				camera.x = 1648;
				camera.y = 68.26f;
				camera.z = -2434;
				camera.rx = 180;
				camera.ry = 10;
			}
		}
		
		if(world != null) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0);
			
			int mvp_scale = (int)(Math.sqrt(Math.max(camera.y / 16 - 8, 0)));
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
				int y = last_mvp_y;
				int z = last_mvp_z;
				
				float angle = (float)Math.toRadians(step);
				mvpMatrix.rotateLocalX(-(float)Math.PI / 2.0f);
				mvpMatrix.rotateZ(0.8f);
				mvpMatrix.rotateY(angle);
				mvpMatrix.translate(-x, -y, -z);
			}
			
			int radius = 8;
			
			if(updateShadowMap) {
				shadowShader.bind();
				shadowShader.setMvpMatrix(mvpMatrix);
				frameBuffer.bindFrameBuffer();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
//				world_render.renderWorld(world, camera, projectionView, radius);
				shadowShader.unbind();
				frameBuffer.unbindFrameBuffer();
			}
			
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffer.getShadowMap());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, atlas.getTextureId());
			
			if(LwjglSettings.useTransparentTextures() || true) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
			
			{
				meshShader.bind();
				meshShader.setShadowMapSpace(MathUtils.getShadowSpaceMatrix(mvpMatrix));
				meshShader.setProjectionView(projectionView);
				world_render.renderWorld(world, camera, projectionView, radius);
				meshShader.unbind();
			}
			
			GL11.glDisable(GL11.GL_BLEND);

			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			
			GL11.glPushMatrix();
			GL11.glLoadMatrixf(projectionView.get(new float[16]));
			drawLoadedChunks(world);
//			drawFrustumChunks(world, camera_view, camera_pos, radius);
			GL11.glPopMatrix();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			GL11.glPushMatrix();
			gui.render();
			GL11.glPopMatrix();
		}
		
		GL11.glPushMatrix();
		menuBar.render();
		GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	
	private int displayList;
	private boolean has_regions;
	void drawLoadedChunks(World world) {
		if(!has_regions) {
			File regions = new File(world.getFolder(), "region");
			if(!regions.isDirectory()) return;
			
			File[] list = regions.listFiles();
			if(list == null) return;
			
			List<IRegion> copy = new ArrayList<>();
			for(File file : list) {
				String[] part = file.getName().split("\\.");
				int x = Integer.valueOf(part[1]);
				int z = Integer.valueOf(part[2]);
				IRegion region = world.getChunkProvider().getRegion(x, z);
				copy.add(region);
			}
			has_regions = true;
			
			displayList = GL11.glGenLists(1);
			GL11.glNewList(displayList, GL11.GL_COMPILE);
			drawRegionGeometryOld(copy);
			GL11.glEndList();
		}
		
		GL11.glCallList(displayList);
	}
	
	void drawRegionGeometryOld(List<IRegion> copy) {
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
					GL11.glColor3f(0.7f, 0.7f, 0.7f);
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
	}
	
	void drawFrustumChunks(World world, Matrix4f projectionView, Vector3f camera, int radius) {
		int x = Math.floorDiv((int)camera.x, 16);
		int z = Math.floorDiv((int)camera.z, 16);
		final int xs = x - radius;
		final int xe = x + radius;
		final int zs = z - radius;
		final int ze = z + radius;
		
		FrustumIntersection intersect = new FrustumIntersection(projectionView);
		
		RenderUtil.drawFrustum(projectionView, camera, LwjglWindow.getWidth(), LwjglWindow.getHeight(), 20, 100);
		
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
