package com.hardcoded.settings;

public interface NumberRange<T> {
	T getMinimum();
	T getMaximum();
	Class<T> getType();
	boolean contains(Number value);
	
	public static NumberRange<Integer> of(final int min, final int max) {
		return new NumberRange<Integer>() {
			@Override
			public Integer getMinimum() {
				return min;
			}
			
			@Override
			public Integer getMaximum() {
				return max;
			}
			
			@Override
			public Class<Integer> getType() {
				return int.class;
			}
			
			@Override
			public boolean contains(Number value) {
				return value.intValue() >= min && value.intValue() <= max;
			}
			
			@Override
			public String toString() {
				return String.format("(%d, %d)", min, max);
			}
		};
	}
	
	public static NumberRange<Float> of(final float min, final float max) {
		return new NumberRange<Float>() {
			@Override
			public Float getMinimum() {
				return min;
			}
			
			@Override
			public Float getMaximum() {
				return max;
			}
			
			@Override
			public Class<Float> getType() {
				return float.class;
			}
			
			@Override
			public boolean contains(Number value) {
				return value.floatValue() >= min && value.floatValue() <= max;
			}
			
			@Override
			public String toString() {
				return String.format("(%.2f, %.2f)", min, max);
			}
		};
	}
	
	public static class NumberRangeException extends Exception {
		private static final long serialVersionUID = 1L;
	}
}
