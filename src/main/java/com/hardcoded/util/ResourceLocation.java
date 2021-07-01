package com.hardcoded.util;

public class ResourceLocation {
	public static String removeNamespace(String key) {
		if(key == null) return null;
		
		int index = key.indexOf(':');
		if(index != -1) key = key.substring(index + 1);
		return key;
	}
}
