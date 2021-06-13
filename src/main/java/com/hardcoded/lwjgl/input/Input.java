package com.hardcoded.lwjgl.input;

import org.lwjgl.glfw.*;

/**
 * @author HardCoded
 */
public class Input {
	public static final boolean[] keys = new boolean[65536];
	public static final boolean[] keys_poll = new boolean[65536];
	public static final boolean[] mouse_buttons = new boolean[256];
	public static double mouse_x = -1;
	public static double mouse_y = -1;
	
	private GLFWKeyCallback keyboard;
	private GLFWCursorPosCallback mouse;
	private GLFWMouseButtonCallback mouse_button;
	
	public Input() {
		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if(key < 0 || key >= keys.length) return;
				
				boolean value = (action != GLFW.GLFW_RELEASE);
				Input.keys[key] = value;
				
				if(action == GLFW.GLFW_RELEASE) {
					Input.keys_poll[key] = false;
				} else if(action == GLFW.GLFW_PRESS) {
					Input.keys_poll[key] = true;
				}
			}
		};
		
		mouse = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				Input.mouse_x = xpos;
				Input.mouse_y = ypos;
			}
		};
		
		mouse_button = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if(button < 0 || button >= mouse_buttons.length) return;
				mouse_buttons[button] = (action != GLFW.GLFW_RELEASE);
			}
		};
	}
	
	public static boolean pollKey(int key) {
		if(key < 0 || key >= keys_poll.length) return false;
		boolean pressed = keys_poll[key];
		keys_poll[key] = false;
		return pressed;
	}
	
	public static float getMouseX() {
		return (float)mouse_x;
	}
	
	public static float getMouseY() {
		return (float)mouse_y;
	}
	
	public static boolean isControlDown() {
		return keys[GLFW.GLFW_KEY_LEFT_CONTROL] || keys[GLFW.GLFW_KEY_RIGHT_CONTROL];
	}
	
	public static boolean isKeyDown(int key) {
		return keys[key];
	}
	
	public static boolean isMouseDown(int key) {
		return mouse_buttons[key];
	}
	
	public GLFWKeyCallback getKeyboard() {
		return keyboard;
	}
	
	public GLFWCursorPosCallback getMouse() {
		return mouse;
	}
	
	public GLFWMouseButtonCallback getMouseButton() {
		return mouse_button;
	}
}
