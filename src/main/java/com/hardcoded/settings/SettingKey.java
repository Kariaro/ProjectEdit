package com.hardcoded.settings;

import java.util.*;

public enum SettingKey {
	MaxFps("max_fps", 120, NumberRange.of(1, Integer.MAX_VALUE)),
	Fov("fov", 90.0f, NumberRange.of(0.0f, 180.0f)),
	RenderDistance("render_distance", 8, NumberRange.of(1, 128)),
	RenderShadows("render_shadows", true),
	AllowTransparentTextures("allow_transparent_textures", false),
	
	ResourcePacks("resource_packs", ""),
	LastResourcePackPath("last_resource_path", ""),
	LastWorldPath("last_world_path", ""),
	;
	
	private static final Set<SettingKey> keys = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(values())));
	
	private final Class<?> fieldType;
	private final String fieldName;
	private final NumberRange<?> range;
	private final Object defaultValue;
	
	private SettingKey(String fieldName, int defaultValue) {
		this(fieldName, int.class, defaultValue, null);
	}
	
	private SettingKey(String fieldName, int defaultValue, NumberRange<?> range) {
		this(fieldName, int.class, defaultValue, range);
	}
	
	private SettingKey(String fieldName, float defaultValue) {
		this(fieldName, float.class, defaultValue, null);
	}
	
	private SettingKey(String fieldName, float defaultValue, NumberRange<?> range) {
		this(fieldName, float.class, defaultValue, range);
	}
	
	private SettingKey(String fieldName, boolean defaultValue) {
		this(fieldName, boolean.class, defaultValue, null);
	}
	
	private SettingKey(String fieldName, String defaultValue) {
		this(fieldName, String.class, defaultValue, null);
	}
	
	private SettingKey(String fieldName, Class<?> fieldType, Object defaultValue, NumberRange<?> range) {
		if(range != null && range.getType() != fieldType)
			throw new IllegalArgumentException(
				"setting '" + fieldName +  "' of type '" + fieldType + "' cannot have a number range of type '" + range.getType() + "'");
		
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.defaultValue = defaultValue;
		this.range = range;
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
	
	/**
	 * Returns the valid number range or {@code null} if all values are allowed.
	 * Note that this {@code NumberRange} will always be the same type as {@link #getFieldType()}
	 */
	public NumberRange<?> getNumberRange() {
		return range;
	}
	
	public static Set<SettingKey> keys() {
		return keys;
	}
}
