package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.util.MathUtils;

/**
 * A simple camera implementation.
 * 
 * @author HardCoded
 */
public class Camera {
	private final long window;
	public float x;
	public float y;
	public float z;
	
	public float rx;
	public float ry;
	public float rz;
	
	public Camera(long window) {
		this.window = window;
	}
	
	private Vector2f mouse = new Vector2f(0, 0);
	private Vector2f delta = new Vector2f(0, 0);
	private void updateMouse() {
		double[] x = new double[1];
		double[] y = new double[1];
		glfwGetCursorPos(window, x, y);
		
		delta.x = mouse.x - (float)x[0];
		delta.y = mouse.y - (float)y[0];
		mouse.x = (float)x[0];
		mouse.y = (float)y[0];
	}
	
	public boolean fast = false;
	public int speedMod = 1;
	public void update() {
		updateMouse();
		
		boolean captureMouse = LwjglWindow.isMouseCaptured();
		
		if(captureMouse) {
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
	
	public Matrix4f getProjectionMatrix(float fov, float width, float height) {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective((float)Math.toRadians(fov), width / height, 0.1f, 10000);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate(-x, -y, -z);
	}
	
	public Matrix4f getProjectionMatrix(float width, float height) {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.setPerspective(MathUtils.toRadians(LwjglConstants.getFov()), width / height, 0.1f, 10000);
		return projectionMatrix
			.rotate(MathUtils.toRadians(ry), 1, 0, 0)
			.rotate(MathUtils.toRadians(rx), 0, 1, 0)
			.rotate(MathUtils.toRadians(rz), 0, 0, 1)
			.translate(-x, -y, -z);
	}
	
	
	
	public float getYaw() {
		return rx;
	}
	
	public float getPitch() {
		return ry;
	}
}
