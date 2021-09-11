package com.hardcoded.mc.versions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MinecraftVersions {
	private static final Map<String, MinecraftVersion> versions = new HashMap<>();
	
	public static MinecraftVersion getVersion(String name) {
		return versions.get(name);
	}
	
	public static Set<String> getVersions() {
		return versions.keySet();
	}
	
	@SafeVarargs
	public static void addVersions(MinecraftVersion... array) {
		for(MinecraftVersion version : array) {
			versions.put(version.getVersionName(), version);
		}
	}
	
	static {
		addVersions(
			MinecraftVersion.of("1.18 experimental snapshot 7", "1.18_experimental-snapshot-7.json", "1.18_experimental-snapshot-7/1.18_experimental-snapshot-7.jar"),
			MinecraftVersion.of("1.17.1", "1.17.1.json", "1.17.1/1.17.1.jar"),
			MinecraftVersion.of("1.14.4", "1.14.4.json", "1.14.4/1.14.4.jar")
		);
	}
}
