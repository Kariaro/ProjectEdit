package com.hardcoded.settings;

import com.hardcoded.settings.NumberRange.NumberRangeException;

class SettingProperty {
	private final SettingKey key;
	private final NumberRange<?> range;
	private Object value;
	
	protected SettingProperty(SettingKey key) {
		this.key = key;
		this.value = key.getDefaultValue();
		this.range = key.getNumberRange();
	}
	
	/**
	 * Returns the field name of this property
	 */
	String getFieldName() {
		return key.getFieldName();
	}
	
	/**
	 * Returns the default value of this property
	 */
	Object getDefaultValue() {
		return key.getDefaultValue();
	}
	
	/**
	 * Returns the string value of this property
	 */
	String getStringDataValue() {
		return value.toString();
	}
	
	/**
	 * Set the value of this property. Calling this method is the
	 * same as calling {@code setStringValue(object.toString())}
	 */
	void setObjectValue(Object value) throws SettingException {
		setStringValue(value == null ? null:value.toString());
	}
	
	/**
	 * Set the string value of this property
	 */
	void setStringValue(String value) throws SettingException {
		Class<?> fieldType = key.getFieldType();
		
		try {
			if(fieldType == int.class) {
				int parsed = Integer.parseInt(value);
				if(range != null && !range.contains(parsed)) {
					throw new NumberRangeException();
				}
				
				this.value = parsed;
			} else if(fieldType == float.class) {
				float parsed = Float.parseFloat(value);
				if(range != null && !range.contains(parsed)) {
					throw new NumberRangeException();
				}
				
				this.value = parsed;
			} else if(fieldType == boolean.class) {
				this.value = Boolean.parseBoolean(value);
			} else if(fieldType == String.class) {
				if(value == null) {
					throw new NullPointerException();
				}
				
				this.value = value;
			} else {
				throw new SettingException(key, "Unsupported fieldType \"" + fieldType+ "\"");
			}
		} catch(NumberFormatException | NullPointerException e) {
			throw new SettingException(key, "Property \"" + key + "\" could not be parsed. \"" + value + "\" was not an \"" + fieldType + "\"", e);
		} catch(NumberRangeException e) {
			throw new SettingException(key, "Property \"" + key + "\" value was outside range. " + value + " was outside the bounds " + range, e);
		}
	}
	
	/**
	 * Returns the int value of this property.
	 * @throws ClassCastException if this property was not an int property
	 */
	int getIntegerValue() {
		return (Integer)this.value;
	}
	
	/**
	 * Returns the float value of this property.
	 * @throws ClassCastException if this property was not a float property
	 */
	float getFloatValue() {
		return (Float)this.value;
	}
	
	/**
	 * Returns the boolean value of this property.
	 * @throws ClassCastException if this property was not a boolean property
	 */
	boolean getBooleanValue() {
		return (Boolean)this.value;
	}
	
	/**
	 * Returns the string value of this property.
	 * @throws ClassCastException if this property was not a string property
	 */
	String getStringValue() {
		return (String)this.value;
	}
	
	/**
	 * Returns the object value of this property
	 */
	Object getObjectValue() {
		return this.value;
	}
}
