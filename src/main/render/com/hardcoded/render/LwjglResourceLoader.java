package com.hardcoded.render;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.mc.general.Minecraft;
import com.hardcoded.mc.general.files.Blocks;
import com.hardcoded.mc.general.world.BlockDataManager;
import com.hardcoded.mc.general.world.IBlockState;
import com.hardcoded.mc.general.world.IBlockState.States;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.mc.objects.MinecraftBlock;
import com.hardcoded.mc.objects.MinecraftBlockState;
import com.hardcoded.mc.versions.MinecraftVersion;
import com.hardcoded.mc.versions.MinecraftVersions;
import com.hardcoded.render.generator.FastModelJsonLoader;
import com.hardcoded.render.generator.FastModelRenderer;
import com.hardcoded.render.generator.VersionResourceReader;
import com.hardcoded.render.gl.ShapeRender;
import com.hardcoded.util.TimerUtils;

public class LwjglResourceLoader {
	private static final Logger LOGGER = LogManager.getLogger(LwjglResourceLoader.class);
	
	protected enum LoadingState {
		STATE_NONE,
		STATE_WAIT,
		STATE_START,
		STATE_GL_OBJECTS,
		STATE_GL_LOAD_ICONS,
		STATE_RENDER;
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
		
		ShapeRender.drawRect(x, y, p, h, 0, 0.6f, 0.3f, 0, 1);
		ShapeRender.drawRect(x, y, w, h, 0, 0.1f, 0.1f, 0.1f, 1);
		ShapeRender.drawRect(x, y, p, h, 0, 0.9f, 0.4f, 0.1f, 1);
		
		loadResources();
	}
	
	public void loadWorld(World world) {
		MinecraftVersion version = MinecraftVersions.getVersion(world.getVersion());
		//version = MinecraftVersions.getVersion("1.18 experimental snapshot 7");
		
		if(version == null) {
			LOGGER.info("Could not find a preset loader for the specified version '{}'", world.getVersion());
			return;
		}
		
		File version_jar = new File(new File(Minecraft.getMinecraftPath(), "versions"), version.getVersionJarPath());
		try {
			// Unload all blocks
			BlockDataManager.unloadBlocks();
			States.unloadStates();
			Blocks.init();
			
			List<MinecraftBlock> blocks = version.getBlocks();
			
			for(MinecraftBlock block : blocks) {
				List<IBlockState> block_states = new ArrayList<>();
				
				for(MinecraftBlockState state : block.getStates()) {
					block_states.add(States.find(state.name, state.values));
				}
				
				// Add the state
				BlockDataManager.addOrGetState(block.getId().toString(), block.isOpaque(), block.getMapColor(), block_states);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//String name = world.getVersion().replaceFirst(" ", "_").replace(' ', '-');
		//File version_jar = new File(Minecraft.getVersionFolder(name), name + ".jar");
		LOGGER.info("Version file: '{}'", version_jar);
		
		try {
			if(reader != null) {
				reader.cleanup();
				reader = null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		String[] packs = {
//			"CubedPack.5.0.2.2.Dev.Old.Font.zip",
//			"VanillaBDcraft 128x MC116.zip",
//			"PureBDcraft 16x MC116.zip"
//			"VanillaTweaks_r776400.zip"
		};
		
		try {
			File resourcepackFolder = new File(Minecraft.getMinecraftPath(), "resourcepacks");
			
			File[] resourcePacks = new File[packs.length];
			for(int i = 0; i < packs.length; i++) {
				resourcePacks[i] = new File(resourcepackFolder, packs[i]);
			}
			
			reader = new VersionResourceReader(version_jar, resourcePacks);
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
				// Unload render cache
				FastModelRenderer.unloadModelCache();
				// Unload texture cache
//				Texture.unloadTextureCache();
				// Unload all textures
				render.textureManager.getBlockAtlas().unload();
				
				TimerUtils.begin();
				
				// Add debug texture
				try {
					render.textureManager.getBlockAtlas().addTexture("projectedit:missing", ImageIO.read(LwjglRender.class.getResourceAsStream("/images/missing.png")));
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
				render.textureManager.getBlockAtlas().reload();
				
				loadingState = LoadingState.STATE_GL_LOAD_ICONS;
			}
			case STATE_GL_LOAD_ICONS: {
				//if(render.textureManager.getIconGenerator().loadIconsPartwise(100, loadingCallback)) {
					// We need to init the gui because otherwise it wont reload icons
					render.gui.init();
					render.biome_blend.load(reader);
					
					long with_gl_nanos = TimerUtils.end();
					LOGGER.info("Took: {} ms to compute gl objects", with_gl_nanos / 1000000.0);
					
					long all_nanos = TimerUtils.end();
					LOGGER.info("Took: {} ms to reload all textures", all_nanos / 1000000.0);
					
					loadingState = LoadingState.STATE_RENDER;
				//}
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
