package com.hardcoded.util;

import java.lang.Math;

import org.joml.*;

public class MathUtils {
	public static final double deg2Rad = Math.PI / 180.0;
	public static final double rad2Deg = 180.0 / Math.PI;
	
	public static float cosDegf(float a) {
		return (float)Math.cos(a * deg2Rad);
	}
	
	public static float sinDegf(float a) {
		return (float)Math.sin(a * deg2Rad);
	}
	
	public static double cosDeg(double a) {
		return Math.cos(a * deg2Rad);
	}
	
	public static double sinDeg(double a) {
		return Math.sin(a * deg2Rad);
	}
	
	public static float toRadians(double deg) {
		return (float)(deg * deg2Rad);
	}
	
	public static boolean fuzzyEquals(double a, double b, double tolerance) {
		return (b >= a - tolerance) && (b <= a + tolerance);
	}
	
	
	public static Matrix4f getOrthoProjectionMatrix(float width, float height, float length) {
		Matrix4f matrix = new Matrix4f();
		matrix.m00( 2f / width);
		matrix.m11( 2f / height);
		matrix.m22(-2f / length);
		matrix.m33(1);
		
		return matrix;
	}
	
	public static Matrix4f getShadowSpaceMatrix(Matrix4f mvpMatrix) {
		return new Matrix4f(new Matrix4f()
			.translate(0.5f, 0.5f, 0.5f)
			.scale(0.5f, 0.5f, 0.5f)
		).mul(mvpMatrix);
	}
	
	public static void decomposeMatrix(Matrix4f matrix, Vector3f translation, Vector3f scale, Quaternionf rotation) {
		throw new UnsupportedOperationException();
	}
}
