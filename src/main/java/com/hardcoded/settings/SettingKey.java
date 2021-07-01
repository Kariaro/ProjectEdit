package com.hardcoded.settings;

import java.util.*;

public enum SettingKey {
	MaxFps("max_fps", 120),
	Fov("fov", 90.0f),
	RenderDistance("render_distance", 8),
	RenderShadows("render_shadows", true),
	AllowTransparentTextures("allow_transparent_textures", false),
	;
	
	private static final Set<SettingKey> keys = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(values())));
	
	private final Class<?> fieldType;
	private final String fieldName;
	private final Object defaultValue;
	
	private SettingKey(String fieldName, int defaultValue) {
		this(fieldName, int.class, defaultValue);
	}
	
	private SettingKey(String fieldName, float defaultValue) {
		this(fieldName, float.class, defaultValue);
	}
	
	private SettingKey(String fieldName, boolean defaultValue) {
		this(fieldName, boolean.class, defaultValue);
	}
	
	private SettingKey(String fieldName, Class<?> fieldType, Object defaultValue) {
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns the type of this property
	 */
	public Class<?> getFieldType() {
		return fieldType;
	}

	/**
	 * Returns the name of this property
	 */
	public String getFieldName() {
		return fieldName;
	}
	
	/**
	 * Returns the default value of this property
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public static Set<SettingKey> keys() {
		return keys;
	}
}
