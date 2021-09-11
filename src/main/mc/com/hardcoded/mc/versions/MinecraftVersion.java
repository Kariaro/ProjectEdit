package com.hardcoded.mc.versions;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardcoded.mc.objects.MinecraftBlock;

public final class MinecraftVersion {
	private static final ObjectMapper json_mapper = new ObjectMapper();
	
	protected List<MinecraftBlock> blocks_cache;
	private final String versionName;
	private final String builtinJson;
	private final String versionJarPath;

	private MinecraftVersion(String versionName, String builtinJson, String versionJarPath) {
		this.versionName = versionName;
		this.versionJarPath = versionJarPath;
		this.builtinJson = builtinJson;
	}

	/**
	 * Returns the name of the version that would show in {@code level.dat}
	 */
	public final String getVersionName() {
		return versionName;
	}

	/**
	 * Returns the path of the version jar file
	 */
	public final String getVersionJarPath() {
		return versionJarPath;
	}
	
	public final String getBuiltinJson() {
		return builtinJson;
	}
	
	public final List<MinecraftBlock> getBlocks() {
		List<MinecraftBlock> blocks = List.of();
		try {
			blocks = json_mapper.readValue(MinecraftVersion.class.getResource("/versions/" + builtinJson), new TypeReference<List<MinecraftBlock>>(){});
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return blocks;
	}
	
	/**
	 * Called when this version was loaded
	public final void load() throws Exception {
		if(builtinJson != null) {
			loadWithJson();
			return;
		}
		
		Thread loading_thread = new Thread(() -> {
			Set<Long> threadIds = Thread.getAllStackTraces().keySet().stream().map(t -> t.getId()).collect(Collectors.toSet());
			URLClassLoader classLoader = null;
			try {
				URL[] defined_urls = getClassLoaderUrls();
				URL[] loader_urls = new URL[defined_urls.length + 1];
				System.arraycopy(defined_urls, 0, loader_urls, 0, defined_urls.length);
				loader_urls[defined_urls.length] = new File(Minecraft.getVersionsPath(), versionJarPath).toURI().toURL();
				
				classLoader = new URLClassLoader(
					loader_urls,
					ClassLoader.getSystemClassLoader().getParent()
				);
				
				Thread.currentThread().setContextClassLoader(classLoader);
				
				onLoad(classLoader);
			} catch(Throwable t) {
				t.printStackTrace();
			} finally {
				// This could create a race condition if a new thread was created while the jar file was loaded
				// Make sure that everything is protected from this.
				
				Set<Thread> threads = Thread.getAllStackTraces().keySet();
				for(Thread thread : threads) {
					System.out.println(thread);
					if(!threadIds.contains(thread.getId())) {
						System.out.println("Interrupt thread: " + thread);
					}
				}
				
				if(classLoader != null) {
					try {
						classLoader.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, "Version Jar Loading Thread");
		
		try {
			loading_thread.start();
			loading_thread.join();
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	@Deprecated
	protected URL[] getClassLoaderUrls() throws Exception {
		return new URL[] {
			new File(Minecraft.getMinecraftPath(), "libraries/org/apache/logging/log4j/log4j-api/2.14.1/log4j-api-2.14.1.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/org/apache/logging/log4j/log4j-core/2.14.1/log4j-core-2.14.1.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/com/mojang/javabridge/1.2.24/javabridge-1.2.24.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/io/netty/netty-all/4.1.9.Final/netty-all-4.1.9.Final.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/it/unimi/dsi/fastutil/8.2.1/fastutil-8.2.1.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/com/mojang/brigadier/1.0.18/brigadier-1.0.18.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/com/google/guava/guava/21.0/guava-21.0.jar").toURI().toURL(),
			new File(Minecraft.getMinecraftPath(), "libraries/com/mojang/datafixerupper/4.0.26/datafixerupper-4.0.26.jar").toURI().toURL()
		};
	}

	@Deprecated
	protected abstract void onLoad(URLClassLoader classLoader) throws Exception;
	
	protected abstract List<Object> get_blocks(URLClassLoader classLoader) throws Exception;
	protected abstract MinecraftBlock convert_block_object(Object blockObject);
	*/
	
	@Override
	public String toString() {
		return "Minecraft[name: \"%s\", path: \"%s\"]".formatted(versionName, versionJarPath);
	}
	
	public static MinecraftVersion of(String versionName, String builtinJson, String versionJarPath) {
		return new MinecraftVersion(versionName, builtinJson, versionJarPath);
	}
}
