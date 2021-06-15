package com.hardcoded.lwjgl.util;

import java.lang.Math;

import org.joml.*;

public class MathUtils {
	public static final float deg2Rad = (float)(Math.PI / 180.0);
	public static final float rad2Deg = (float)(180.0 / Math.PI);
	
	public static float cosDeg(float a) {
		return (float)Math.cos(a * deg2Rad);
	}
	
	public static float sinDeg(float a) {
		return (float)Math.sin(a * deg2Rad);
	}
	
	public static float toRadians(double deg) {
		return (float)(deg * deg2Rad);
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
}
