package com.hardcoded.lwjgl.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * @author HardCoded
 */
public class Input {
	public static final boolean[] keys = new boolean[65536];
	public static double mouse_x;
	public static double mouse_y;
	
	private GLFWKeyCallback keyboard;
	private GLFWCursorPosCallback mouse;
	
	public Input() {
		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(key < 0 || key >= keys.length) return;
				Input.keys[key] = (action != GLFW.GLFW_RELEASE);
			}
		};
		
		 mouse = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				Input.mouse_x = xpos;
				Input.mouse_y = ypos;
			}
		};
	}
	
	public static boolean pollKey(int key) {
		if(key < 0 || key >= keys.length) return false;
		boolean pressed = keys[key];
		keys[key] = false;
		return pressed;
	}
	
	public static float getMouseX() {
		return (float)mouse_x;
	}
	
	public static float getMouseY() {
		return (float)mouse_y;
	}
	
	public GLFWKeyCallback getKeyboard() {
		return keyboard;
	}
	
	public GLFWCursorPosCallback getMouse() {
		return mouse;
	}
}
