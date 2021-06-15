package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.util.MathUtils;

/**
 * A simple camera implementation
 * 
 * @author HardCoded
 */
public class Camera {
	public float x;
	public float y;
	public float z;
	
	public float rx;
	public float ry;
	public float rz;
	
	private Vector2f mouse = new Vector2f(0, 0);
	private Vector2f delta = new Vector2f(0, 0);
	private void updateMouse() {
		float cx = Input.getMouseX();
		float cy = Input.getMouseY();
		delta.x = mouse.x - cx;
		delta.y = mouse.y - cy;
		mouse.x = cx;
		mouse.y = cy;
	}
	
	public int speedMod = 1;
	public void update() {
		updateMouse();
		
		if(LwjglWindow.isMouseCaptured()) {
			rx -= delta.x / 2.0f;
			ry -= delta.y / 2.0f;
		}
		
		if(ry < -90) ry = -90;
		if(ry >  90) ry =  90;
		
		if(rx <   0) rx += 360;
		if(rx > 360) rx -= 360;
		
		boolean forwards = Input.isKeyDown(GLFW_KEY_W);
		boolean right = Input.isKeyDown(GLFW_KEY_A);
		boolean left = Input.isKeyDown(GLFW_KEY_D);
		boolean backwards = Input.isKeyDown(GLFW_KEY_S);
		boolean up = Input.isKeyDown(GLFW_KEY_SPACE);
		boolean down = Input.isKeyDown(GLFW_KEY_LEFT_SHIFT);
		if(Input.pollKey(GLFW_KEY_LEFT_CONTROL)) {
			speedMod++;
			if(speedMod > 4) {
				speedMod = 1;
			}
		}
		
		int xd = 0;
		int yd = 0;
		int zd = 0;
		
		if(forwards) zd ++;
		if(backwards) zd --;
		if(right) xd --;
		if(left) xd ++;
		if(up) yd ++;
		if(down) yd --;
		
		float xx = xd * MathUtils.cosDeg(rx) + zd * MathUtils.sinDeg(rx);
		float zz = xd * MathUtils.sinDeg(rx) - zd * MathUtils.cosDeg(rx);
		float yy = yd;
		
		float time_delta = LwjglWindow.getDeltaTime();
		float speed = 10 * (float)Math.pow(5, speedMod - 1);
		speed *= time_delta;
		
		x += xx * speed;
		y += yy * speed;
		z += zz * speed;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(x, y, z);
	}
	
	public Matrix4f getViewMatrix() {
		return new Matrix4f()
			.rotate(MathUtils.toRadians(rx), 1, 0, 0)
			.rotate(MathUtils.toRadians(ry), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate(-x, -y, -z);
	}
	

	public float near = 0.1f;
	public float far = 100000; // 10000
	public Matrix4f getProjectionMatrix() {
		float width = LwjglWindow.getWidth();
		float height = LwjglWindow.getHeight();
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(MathUtils.toRadians(LwjglSettings.getFov()), width / height, near, far);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate(-x, -y, -z);
	}
	
	public Matrix4f getProjectionMatrixNoTranslate() {
		float width = LwjglWindow.getWidth();
		float height = LwjglWindow.getHeight();
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(MathUtils.toRadians(LwjglSettings.getFov()), width / height, near, far);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1);
	}
	
	
	
	public float getYaw() {
		return rx;
	}
	
	public float getPitch() {
		return ry;
	}
	
	/**
	 * Returns the raycast of the ray at the specified pixel position
	 */
	public Vector3f getScreenRaycast(float x, float y) {
		int width = LwjglWindow.getWidth();
		int height = LwjglWindow.getHeight();
		return getProjectionMatrixNoTranslate()
			.unproject(x, height - y, 1, new int[] { 0, 0, width, height }, new Vector3f());
	}
	
//	public Vector3f getScreenRaycast(float x, float y) {
//		int width = LwjglWindow.getWidth();
//		int height = LwjglWindow.getHeight();
//		return getProjectionMatrixNoTranslate().unproject(x, height - y, 1, new int[] { 0, 0, width, height }, new Vector3f());
//	}
}
