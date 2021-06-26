package com.hardcoded.render;

import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.utils.FastModelJsonLoader;
import com.hardcoded.utils.TimerUtils;
import com.hardcoded.utils.VersionResourceReader;

public class LwjglResourceLoader {
	private static final Logger LOGGER = LogManager.getLogger(LwjglResourceLoader.class);
	
	protected enum LoadingState {
		STATE_NONE,
		STATE_WAIT,
		STATE_START,
		STATE_GL_OBJECTS,
		STATE_GL_LOAD_ICONS,
		STATE_RENDER,;
	}
	
	private final BiConsumer<Integer, Integer> loadingCallback;
	private final LwjglRender render;
	
	private LoadingState loadingState = LoadingState.STATE_NONE;
	private VersionResourceReader reader;
	private float loadingPercentage;
	
	public LwjglResourceLoader(LwjglRender render) {
		this.loadingCallback = (count, size) -> {
			loadingPercentage = count / (float)size;
		};
		this.render = render;
	}
	
	public void render() {
		if(!isLoading()) return;
		
		int x = 20;
		int h = 20;
		int y = (int)((LwjglWindow.getHeight() - h) / 2.0f);
		int w = LwjglWindow.getWidth() - x * 2;
		
		float p = w * loadingPercentage;
		GL11.glColor4f(0.6f, 0.3f, 0, 1);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2f(x  -3, y  +3);
			GL11.glVertex2f(x  -3, y+h+3);
			GL11.glVertex2f(x+p-3, y+h+3);
			GL11.glVertex2f(x+p-3, y  +3);
		GL11.glEnd();
		
		GL11.glColor4f(0.1f, 0.1f, 0.1f, 1);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2f(x  , y  );
			GL11.glVertex2f(x  , y+h);
			GL11.glVertex2f(x+w, y+h);
			GL11.glVertex2f(x+w, y  );
		GL11.glEnd();
		
		GL11.glColor4f(0.9f, 0.4f, 0.1f, 1);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2f(x  , y  );
			GL11.glVertex2f(x  , y+h);
			GL11.glVertex2f(x+p, y+h);
			GL11.glVertex2f(x+p, y  );
		GL11.glEnd();
		
		loadResources();
	}
	
	public void loadWorld(World world) {
		File version_jar = new File(Minecraft.getVersionFolder(world.getVersion()), world.getVersion() + ".jar");
		LOGGER.info("Version file: '{}'", version_jar);
		
		try {
			reader = new VersionResourceReader(version_jar);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		loadingState = LoadingState.STATE_START;
	}
	
	public void reload() {
		loadingState = LoadingState.STATE_START;
	}
	
	private void loadResources() {
		switch(loadingState) {
			case STATE_NONE:
			case STATE_WAIT:
			case STATE_RENDER:
				return;
			
			case STATE_START: {
				TimerUtils.begin();
				// Unload rendered chunks
				render.world_render.unloadCache();
				// Unload model cache
				FastModelJsonLoader.unloadCache();
				// Unload all textures
				render.atlas.unload();
				
				TimerUtils.begin();
				
				// Add debug texture
				try {
					render.atlas.addTexture("projectedit:debug_faces", ImageIO.read(LwjglRender.class.getResourceAsStream("/images/debug_faces.png")));
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				loadingState = LoadingState.STATE_WAIT;
				Thread nonGlThread = new Thread(() -> {
					// Does not require GL
					reader.loadBlocks(loadingCallback);
					
					long without_gl_nanos = TimerUtils.end();
					LOGGER.info("Took: {} ms to compute non gl objects", without_gl_nanos / 1000000.0);
					
					loadingState = LoadingState.STATE_GL_OBJECTS;
				}, "Loading NonGL");
				nonGlThread.setDaemon(true);
				nonGlThread.start();
				return;
			}
			case STATE_GL_OBJECTS: {
				TimerUtils.begin();
				
				// Recompile textures
				render.atlas.reload();
				
				loadingState = LoadingState.STATE_GL_LOAD_ICONS;
			}
			case STATE_GL_LOAD_ICONS: {
				if(render.textureManager.getIconGenerator().loadIconsPartwise(100, loadingCallback)) {
					// We need to init the gui because otherwise it wont reload icons
					render.gui.init();
					
					long with_gl_nanos = TimerUtils.end();
					LOGGER.info("Took: {} ms to compute gl objects", with_gl_nanos / 1000000.0);
					
					long all_nanos = TimerUtils.end();
					LOGGER.info("Took: {} ms to reload all textures", all_nanos / 1000000.0);
					
					loadingState = LoadingState.STATE_RENDER;
				}
			}
		}
	}

	public boolean isLoading() {
		return loadingState == LoadingState.STATE_WAIT
			|| loadingState == LoadingState.STATE_START
			|| loadingState == LoadingState.STATE_GL_OBJECTS
			|| loadingState == LoadingState.STATE_GL_LOAD_ICONS;
	}

	public boolean shouldRender() {
		return loadingState == LoadingState.STATE_RENDER;
	}
}
