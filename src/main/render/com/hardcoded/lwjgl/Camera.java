package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.settings.ProjectSettings;
import com.hardcoded.util.MathUtils;

/**
 * A simple camera implementation
 * 
 * @author HardCoded
 */
public class Camera {
	public double x;
	public double y;
	public double z;
	
	public double rx;
	public double ry;
	public double rz;
	
	private Vector2f mouse = new Vector2f(0, 0);
	private Vector2f delta = new Vector2f(0, 0);
	private float speed = 1;
	
	public float near = 0.1f;
	public float far = 100000; // 10000
	
	private void updateMouse() {
		float cx = Input.getMouseX();
		float cy = Input.getMouseY();
		delta.x = mouse.x - cx;
		delta.y = mouse.y - cy;
		mouse.x = cx;
		mouse.y = cy;
		
		speed += Input.getScrollDeltaY() / 10.0;
		if(speed < 0) speed = 0;
		if(speed > 5) speed = 5;
	}
	
	public void update() {
		updateMouse();
		
		if(LwjglWindow.isMouseCaptured()) {
			rx -= delta.x / 4.0;
			ry -= delta.y / 4.0;
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
		int xd = 0;
		int yd = 0;
		int zd = 0;
		
		if(forwards) zd ++;
		if(backwards) zd --;
		if(right) xd --;
		if(left) xd ++;
		if(up) yd ++;
		if(down) yd --;
		
		double xx = xd * MathUtils.cosDeg(rx) + zd * MathUtils.sinDeg(rx);
		double zz = xd * MathUtils.sinDeg(rx) - zd * MathUtils.cosDeg(rx);
		double yy = yd;
		
		double time_delta = LwjglWindow.getDeltaTime();
		double speed = this.speed * 10 * Math.pow(5, this.speed - 1);
		speed *= time_delta;
		
		x += xx * speed;
		y += yy * speed;
		z += zz * speed;
	}
	
	public Vector3f getPosition() {
		return new Vector3f((float)x, (float)y, (float)z);
	}
	
	public Matrix4f getViewMatrix() {
		return new Matrix4f()
			.rotate(MathUtils.toRadians(rx), 1, 0, 0)
			.rotate(MathUtils.toRadians(ry), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate((float)-x, (float)-y, (float)-z);
	}
	
	public Matrix4f getProjectionMatrix() {
		float width = LwjglWindow.getWidth();
		float height = LwjglWindow.getHeight();
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(MathUtils.toRadians(ProjectSettings.getFov()), width / height, near, far);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate((float)-x, (float)-y, (float)-z);
	}
	
	public Matrix4f getProjectionMatrixTest() {
		float width = LwjglWindow.getWidth();
		float height = LwjglWindow.getHeight();
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(MathUtils.toRadians(ProjectSettings.getFov()), width / height, near, far);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate(test(-x), (float)-y, test(-z));
	}
	
	private float test(double value) {
		if(value > -1 && value < 1) {
			return (float)value;
		}
		
		if(value < 0) {
			int coord = (int)(value / 16) * 16;
			return (float)(value - coord);
		}
		
		int coord = (int)((value - 1) / 16) * 16;
		return (float)(value - coord - 16);
	}
	
	public Matrix4f getChunkMatrix(int x, int z) {
		float width = LwjglWindow.getWidth();
		float height = LwjglWindow.getHeight();
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(MathUtils.toRadians(ProjectSettings.getFov()), width / height, near, far);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate((float)(-this.x + x), (float)-y, (float)(-this.z + z));
	}
	
	/**
	 * Returns the raycast of the ray at the specified pixel position
	 */
	public Vector3f getScreenRaycast(float x, float y) {
		int width = LwjglWindow.getWidth();
		int height = LwjglWindow.getHeight();
		
		return new Matrix4f()
			.setPerspective(MathUtils.toRadians(ProjectSettings.getFov()), width / (float)height, near, far)
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.unproject(x, height - y, 1, new int[] { 0, 0, width, height }, new Vector3f());
	}
}
