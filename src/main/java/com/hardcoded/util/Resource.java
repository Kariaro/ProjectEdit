package com.hardcoded.util;

import java.util.Objects;

public class Resource {
	public final String domain;
	public final String path;
	
	protected Resource(String key) {
		key = key.toLowerCase();
		
		int index = key.indexOf(':');
		if(index < 0) {
			domain = key;
			path = "";
		} else {
			domain = key.substring(0, index);
			path = key.substring(index + 1);
		}
	}
	
	protected Resource(String domain, String path) {
		this.domain = domain.toLowerCase();
		this.path = path.toLowerCase();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(domain, path);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Resource)) return false;
		Resource that = (Resource)obj;
		return this.domain.equals(that.domain)
			&& this.path.equals(that.path);
	}
	
	@Override
	public String toString() {
		return domain + ':' + path;
	}
	
	public static Resource of(String domain, String path) {
		return new Resource(domain, path);
	}

	public static Resource of(String name) {
		return new Resource(name);
	}
	
	public static String removeNamespace(String key) {
		if(key == null) return null;
		
		int index = key.indexOf(':');
		if(index != -1) key = key.substring(index + 1);
		return key;
	}
}
