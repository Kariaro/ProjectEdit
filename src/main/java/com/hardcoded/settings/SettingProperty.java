package com.hardcoded.settings;

public class SettingProperty {
	private final SettingKey key;
	private Object value;
	
	protected SettingProperty(SettingKey key) {
		this.key = key;
		this.value = key.getDefaultValue();
	}
	
	/**
	 * Returns the field name of this property
	 */
	public String getFieldName() {
		return key.getFieldName();
	}
	
	/**
	 * Returns the default value of this property
	 */
	public Object getDefaultValue() {
		return key.getDefaultValue();
	}
	
	/**
	 * Returns the string value of this property
	 */
	public String getStringValue() {
		return value.toString();
	}
	
	/**
	 * Set the value of this property. Calling this method is the
	 * same as calling {@code setStringValue(object.toString())}
	 */
	public void setObjectValue(Object value) {
		setStringValue(value == null ? null:value.toString());
	}
	
	/**
	 * Set the string value of this property
	 */
	public void setStringValue(String value) {
		Class<?> fieldType = key.getFieldType();
		
		try {
			if(fieldType == int.class) {
				this.value = Integer.parseInt(value);
			} else if(fieldType == float.class) {
				this.value = Float.parseFloat(value);
			} else if(fieldType == boolean.class) {
				this.value = Boolean.parseBoolean(value);
			} else {
				throw new UnsupportedOperationException("Invalid setting fieldType '" + fieldType + "'");
			}
		} catch(NumberFormatException | NullPointerException e) {
			ProjectSettings.LOGGER.warn("Property value could not be parsed. '" + value + "' was not an '" + fieldType + "'");
			this.value = key.getDefaultValue();
		}
	}
	
	/**
	 * Returns the int value of this property.
	 * @throws ClassCastException if this property was not an int property
	 */
	public int getIntegerValue() {
		return (Integer)this.value;
	}
	
	/**
	 * Returns the float value of this property.
	 * @throws ClassCastException if this property was not a float property
	 */
	public float getFloatValue() {
		return (Float)this.value;
	}
	
	/**
	 * Returns the boolean value of this property.
	 * @throws ClassCastException if this property was not a boolean property
	 */
	public boolean getBooleanValue() {
		return (Boolean)this.value;
	}
}
