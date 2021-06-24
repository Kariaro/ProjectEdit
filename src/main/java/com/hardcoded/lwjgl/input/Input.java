package com.hardcoded.lwjgl.input;

import org.lwjgl.glfw.*;

import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.*;
import com.hardcoded.render.gui.GuiRender;

/**
 * @author HardCoded
 */
public class Input {
	private static final boolean[] keys = new boolean[65536];
	private static final boolean[] keys_poll = new boolean[65536];
	private static final boolean[] mouse_buttons = new boolean[64];
	
	private static boolean has_focus;
	
	// Gui
	private static GuiListener focused_element;
	private static boolean sendGuiEvents = true;
	
	// Contains the last key modifiers
	private static int LAST_MODIFIERS = 0;
	
	private static double mouse_x = -1;
	private static double mouse_y = -1;
	private static double scroll_delta_y = 0;
	private static double scroll_delta_x = 0;
	
	private GLFWKeyCallback keyboard;
	private GLFWCursorPosCallback mouse_move;
	private GLFWMouseButtonCallback mouse_button;
	private GLFWScrollCallback mouse_wheel;
	private GLFWWindowFocusCallback window_focus;
	
	public Input(final GuiRender gui) {
		keyboard = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				LAST_MODIFIERS = mods;
				
				if(key < 0 || key >= keys.length) return;
				boolean value = (action != GLFW.GLFW_RELEASE);
				Input.keys[key] = value;
				
				if(action == GLFW.GLFW_RELEASE) {
					Input.keys_poll[key] = false;
				} else if(action == GLFW.GLFW_PRESS) {
					Input.keys_poll[key] = true;
				}
				
				if(sendGuiEvents && has_focus) {
					GuiKeyEvent event = new GuiKeyEvent(key, action, mods);
					if(focused_element != null) {
						focused_element.onKeyEvent(event);
					} else {
						gui.processKeyEvent(event);
					}
					
					if(event.isConsumed()) {
						Input.keys_poll[key] = false;
						Input.keys[key] = false;
					}
				}
			}
		};
		
		mouse_button = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				LAST_MODIFIERS = mods;
				
				if(button < 0 || button >= mouse_buttons.length) return;
				mouse_buttons[button] = (action != GLFW.GLFW_RELEASE);
				
				if(sendGuiEvents && has_focus) {
					GuiMouseEvent event = new GuiMousePress((float)mouse_x, (float)mouse_y, button, action, mods);
					focused_element = gui.processMouseEvent(event);
					if(action == GLFW.GLFW_RELEASE) {
						focused_element = null;
					}
				}
			}
		};
		
		mouse_move = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				Input.mouse_x = xpos;
				Input.mouse_y = ypos;
				
				if(sendGuiEvents && has_focus) {
					GuiMouseEvent event = new GuiMouseMove((float)xpos, (float)ypos, LAST_MODIFIERS);
					
					if(focused_element != null) {
						focused_element.onMouseEvent(event);
					} else {
						gui.processMouseEvent(event);
					}
				}
			}
		};
		
		mouse_wheel = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				if(sendGuiEvents) {
					GuiMouseEvent event = new GuiMouseScroll((float)mouse_x, (float)mouse_y, (float)yoffset, LAST_MODIFIERS);
					if(focused_element != null) {
						focused_element.onMouseEvent(event);
					} else {
						gui.processMouseEvent(event);
					}
					
					if(!event.isConsumed()) {
						scroll_delta_x = xoffset;
						scroll_delta_y = yoffset;
					}
				} else {
					scroll_delta_x = xoffset;
					scroll_delta_y = yoffset;
				}
			}
		};
		
		window_focus = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				Input.has_focus = focused;
				Input.focused_element = null;
			}
		};
	}
	
	public GLFWKeyCallback getKeyboard() {
		return keyboard;
	}
	
	public GLFWCursorPosCallback getMouse() {
		return mouse_move;
	}
	
	public GLFWMouseButtonCallback getMouseButton() {
		return mouse_button;
	}
	
	public GLFWScrollCallback getMouseWheel() {
		return mouse_wheel;
	}
	
	public GLFWWindowFocusCallback getFocusCallback() {
		return window_focus;
	}
	
	// GLOBAL
	
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
	
	public static float getScrollDeltaX() {
		return (float)scroll_delta_x;
	}
	
	public static float getScrollDeltaY() {
		return (float)scroll_delta_y;
	}
	
	/**
	 * This method is used to flush delta states that should be reset after
	 * calling <code>GLFW.glfwPollEvents();</code>
	 */
	public static void flush() {
		scroll_delta_x = 0;
		scroll_delta_y = 0;
	}
	
	public static boolean isControlDown() {
		return keys[GLFW.GLFW_KEY_LEFT_CONTROL] || keys[GLFW.GLFW_KEY_RIGHT_CONTROL];
	}
	
	public static boolean isShiftDown() {
		return keys[GLFW.GLFW_KEY_LEFT_SHIFT] || keys[GLFW.GLFW_KEY_RIGHT_SHIFT];
	}
	
	public static boolean isAltDown() {
		return keys[GLFW.GLFW_KEY_LEFT_ALT] || keys[GLFW.GLFW_KEY_RIGHT_ALT];
	}
	
	public static boolean isKeyDown(int key) {
		return keys[key];
	}
	
	public static boolean isMouseDown(int key) {
		return mouse_buttons[key];
	}
	
	public static boolean hasFocus() {
		return has_focus;
	}

	public static boolean isFocused(Object obj) {
		return obj == focused_element;
	}
	
	@Deprecated
	public static void sendGuiEvents(boolean enable) {
		Input.sendGuiEvents = enable;
	}
}
