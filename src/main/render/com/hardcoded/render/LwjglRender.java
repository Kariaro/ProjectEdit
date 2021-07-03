package com.hardcoded.render;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.hardcoded.lwjgl.framebuffer.Framebuffer;
import com.hardcoded.lwjgl.framebuffer.ShadowFramebuffer;
import com.hardcoded.lwjgl.icon.TextureManager;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.mesh.Mesh;
import com.hardcoded.lwjgl.shader.MeshShader;
import com.hardcoded.lwjgl.shader.ShadowShader;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.render.gui.GuiRender;
import com.hardcoded.render.menubar.MenuBar;
import com.hardcoded.render.util.MeshBuffer;
import com.hardcoded.render.util.RenderUtil;
import com.hardcoded.settings.ProjectSettings;
import com.hardcoded.util.MathUtils;

public class LwjglRender {
	private static final Logger LOGGER = LogManager.getLogger(LwjglRender.class);
	
	private final long window;
	private MenuBar menuBar;
	
	private LwjglResourceLoader resourceLoader;
	protected TextureManager textureManager;
	protected BiomeBlend biome_blend;
	protected WorldRender world_render;
	protected GuiRender gui;
	
	public ShadowShader shadowShader;
	public MeshShader meshShader;
	
	private World last_world;
	// Initialized from ProjectEdit.getInstance().getCamera()
	private Camera camera;
	
	public LwjglRender(long window, int width, int height) {
		this.window = window;
		
		camera = ProjectEdit.getInstance().getCamera();
		gui = new GuiRender();
		biome_blend = new BiomeBlend();
		menuBar = new MenuBar();
		world_render = new WorldRender();
		
		resourceLoader = new LwjglResourceLoader(this);
		
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
	
	private void init() {
		Blocks.init();
		
		try {
			shadowShader = new ShadowShader();
			meshShader = new MeshShader();
			
			ShadowFramebuffer shadow = new ShadowFramebuffer(8192, 8192);
			shadowBuffer = shadow;
			textureShadowMap = shadow.getShadowMap();
			
			textureManager = ProjectEdit.getInstance().getTextureManager();
			textureManager.init();
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
	private boolean last_shadow_key = false;
	public Framebuffer shadowBuffer;
	private int textureShadowMap;
	
	protected Vector3f camera_pos;
	protected Matrix4f camera_view;
	
	public void render() {
		GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		resourceLoader.render();
		
		// Get the current loaded world
		World world = ProjectEdit.getInstance().getWorld();
		
		if(!resourceLoader.isLoading()) {
			if(last_world != world) {
				last_world = world;
				world_render.unloadCache();
				
				if(world != null) {
					regions_mesh = null;
					resourceLoader.loadWorld(world);
					
//					camera.x = 0;
//					camera.y = 10;
//					camera.z = 0;
//					camera.rx = 45;
//					camera.ry = 45;
				}
			}
		}
		
//		ProjectSettings.setRenderShadows(true);
//		ProjectSettings.setMaxFps(2400);
		
		if(resourceLoader.shouldRender()) {
			if(Input.isKeyDown(GLFW.GLFW_KEY_U) || camera_pos == null) {
				camera_pos = camera.getPosition();
				camera_view = camera.getProjectionMatrix();
			}
			
			if(Input.isControlDown()) {
				if(Input.pollKey(GLFW.GLFW_KEY_F5)) {
					gui.init();
				}
				
				
				if(Input.pollKey(GLFW.GLFW_KEY_T)) {
					resourceLoader.reload();
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
				
				if(Input.isKeyDown(GLFW.GLFW_KEY_3)) {
//					camera.x=2.331389f;camera.y=79.999603f;camera.z=-187.179398f;camera.rx=108.250000f;camera.ry=34.750000f;
					camera.x=1702.766724f;camera.y=139.736618f;camera.z=-2339.164307f;camera.rx=151.000000f;camera.ry=67.750000f;
//					camera.x=1698.938843f;camera.y=66.289268f;camera.z=-2353.702881f;camera.rx=141.500000f;camera.ry=34.750000f;
				}
				
				if(Input.isKeyDown(GLFW.GLFW_KEY_9)) {
					System.out.printf(Locale.US, "camera.x=%ff;camera.y=%ff;camera.z=%ff;camera.rx=%ff;camera.ry=%ff;\n", camera.x, camera.y, camera.z, camera.rx, camera.ry);
				}
			}
			
			
			if(world != null) {
				Matrix4f projectionView = camera.getProjectionMatrix();
				
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
				
				if(last_shadow_key != ProjectSettings.getRenderShadows()) {
					last_shadow_key = ProjectSettings.getRenderShadows();
					updateShadowMap = true;
				}
				
				Matrix4f mvpMatrix = MathUtils.getOrthoProjectionMatrix(512 * mvp_scale, 512 * mvp_scale, 400);
				
				{
					float step = (System.currentTimeMillis() % 72000L) / 4000.0f;
					step = 45;
					step = (int)(step);
					
					float angle = (float)Math.toRadians(step);
					mvpMatrix.rotateLocalX(-(float)Math.PI / 2.0f);
					mvpMatrix.rotateZ(0.8f);
					mvpMatrix.rotateY(angle);
				}
				
				int radius = ProjectSettings.getRenderDistance();
				
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
				
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				
				if(updateShadowMap || world_render.new_chunk_loaded) {
					world_render.new_chunk_loaded = false;
					shadowShader.bind();
					shadowShader.setMvpMatrix(mvpMatrix);
					
					shadowBuffer.bind();
					if(ProjectSettings.getRenderShadows()) {
						world_render.renderWorld(world, shadowShader, camera, projectionView, radius, 0);
					}
					shadowShader.unbind();
					shadowBuffer.unbind();
				}
				
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureShadowMap);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				textureManager.getBlockAtlas().bind();
				
				if(ProjectSettings.useTransparentTextures()) {
					
				}
				
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glDisable(GL11.GL_BLEND);
				
				{
					meshShader.bind();
					meshShader.setShadowMapSpace(MathUtils.getShadowSpaceMatrix(mvpMatrix));
					meshShader.setProjectionView(projectionView);
					meshShader.setTranslationMatrix(new Matrix4f());
					
					meshShader.setUseShadows(false);
					meshShader.setUseOnlyColors(true);
//					GL11.glDisable(GL11.GL_CULL_FACE);
					drawLoadedChunks(world);
					GL11.glEnable(GL11.GL_CULL_FACE);
					
					meshShader.setUseShadows(true);
					meshShader.setUseOnlyColors(false);
					world_render.renderWorld(world, meshShader, camera, projectionView, radius,
						WorldRender.FRUSTUM_CULLING |
						WorldRender.DRAW_TRANSLUCENT
					);
					
					meshShader.unbind();
				}

				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_BLEND);
				
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
				
				
				GL11.glPushMatrix();
				GL11.glLoadMatrixf(projectionView.get(new float[16]));
				
//				{
//					float size = 256;
//					float height = 200;
//					
//					GL11.glColor3f(1, 1, 1);
//					GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//						GL11.glTexCoord2i(0, 0); GL11.glVertex3f(-size, height, -size);
//						GL11.glTexCoord2i(0, 1); GL11.glVertex3f(-size, height,  size);
//						GL11.glTexCoord2i(1, 1); GL11.glVertex3f( size, height,  size);
//						GL11.glTexCoord2i(1, 0); GL11.glVertex3f( size, height, -size);
//					GL11.glEnd();
//				}
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//				drawFrustumChunks(world, camera_view, camera_pos, radius);
				GL11.glPopMatrix();
				
				GL11.glPushMatrix();
				
//				{
//					Position pos = WorldUtils.raycastBlock(world, camera.getPosition().sub(0, 0.9f, 0, new Vector3f()), new Vector3f(0, 1, 0), 0.5f);
//					if(pos != null) {
//						IBlockData data = world.getBlock(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
//						if(data != null) {
//							if(data.getBlockId() == Blocks.WATER.getBlockId()) {
//								int w = LwjglWindow.getWidth();
//								int h = LwjglWindow.getHeight();
//								
//								GL11.glDisable(GL11.GL_DEPTH_TEST);
//								GL11.glEnable(GL11.GL_BLEND);
//								GL11.glColor4f(0, 0, 0.3f, 0.6f);
//								GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//									GL11.glVertex2i(0, 0);
//									GL11.glVertex2i(0, h);
//									GL11.glVertex2i(w, h);
//									GL11.glVertex2i(w, 0);
//								GL11.glEnd();
//								GL11.glDisable(GL11.GL_BLEND);
//							}
//						}
//					}
//				}
				gui.render();
				GL11.glPopMatrix();
			}
		}
		
		GL11.glPushMatrix();
		menuBar.render();
		GL11.glPopMatrix();
	}
	
	private Mesh regions_mesh;
	private void drawLoadedChunks(World world) {
		if(regions_mesh == null) {
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
			
			MeshBuffer buffer = new MeshBuffer();
			drawRegionGeometry(buffer, copy);
			for(IRegion region : copy) {
				if(!(region instanceof Region)) continue;
				((Region)region).unloadRegion();
			}
			
			regions_mesh = buffer.build();
		}
		
		regions_mesh.render();
	}
	
	void drawRegionGeometry(MeshBuffer buffer, List<IRegion> copy) {
		float yo = -0.1f;
		
		float[] color_odd  = { 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f, 0.7f }; 
		float[] color_even = { 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f }; 
		for(IRegion region : copy) {
			if(region.getStatus() != Status.LOADED) continue;
			
			int rx = region.getX() * 512;
			int rz = region.getZ() * 512;
			
			final int len = 32 * 32;
			for(int i = 0; i < len; i++) {
				int x = i & 31;
				int z = i / 32;
				
				int gx = rx + x * 16;
				int gz = rz + z * 16;
				
				if(region.hasChunk(x, z)) {
					buffer.color(((x ^ z) & 1) == 0 ? color_even:color_odd);
					buffer.uv(new float[12]);
					
					buffer.pos(gx     , yo, gz     );
					buffer.pos(gx     , yo, gz + 16);
					buffer.pos(gx + 16, yo, gz + 16);
					
					buffer.pos(gx     , yo, gz     );
					buffer.pos(gx + 16, yo, gz + 16);
					buffer.pos(gx + 16, yo, gz     );
				}
			}
		}
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
				if(chunk == null) continue;
				
				if(chunk.getStatus() != Status.LOADED) {
					GL11.glColor4f(1, 1, 1, 1);
				} else {
					GL11.glColor4f(1, 0, 0, 1);
				}
				
				for(int k = 0; k < 16; k++) {
					if(intersect.testAab(new Vector3f(i * 16, k * 16, j * 16), new Vector3f((i + 1) * 16, (k + 1) * 16, (j + 1) * 16))) {
						if(chunk.getStatus() != Status.LOADED) {
							RenderUtil.drawWireBlock(i * 16, 0, j * 16, 16, 16, 16);
							break;
						}
						
						IChunkSection section = chunk.getSection(k);
						if(section == null) continue;
						
						if(!section.isLoaded()) {
							continue;
						}
						
						RenderUtil.drawWireBlock(i * 16, k * 16, j * 16, 16, 16, 16);
					}
				}
			}
		}
	}
}
