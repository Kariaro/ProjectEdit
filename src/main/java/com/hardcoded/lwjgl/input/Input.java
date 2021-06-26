package com.hardcoded.lwjgl.input;

import org.lwjgl.glfw.*;

import com.hardcoded.lwjgl.input.InputMask.Mask;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.*;

/**
 * @author HardCoded
 */
public class Input {
	private static final boolean[] keys = new boolean[65536];
	private static final boolean[] keys_poll = new boolean[65536];
	private static final boolean[] mouse_buttons = new boolean[16];
	
	private static boolean has_focus;
	
	// Gui
	private static GuiListener mouse_holder;
	private static boolean has_mouse_holder;
	
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
	
	public Input() {
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
				
				GuiKeyEvent event = new GuiKeyEvent(key, action, mods);
				
				if(InputMask.focusedListener != null) {
					InputMask.focusedListener.onKeyEvent(event);
				}
				
				for(GuiListener listener : InputMask.listeners) {
					listener.onKeyEvent(event);
				}
			}
		};
		
		mouse_button = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				LAST_MODIFIERS = mods;
				
				if(button < 0 || button >= mouse_buttons.length) return;
				
				Mask mask = InputMask.getFirstIntersection((float)mouse_x, (float)mouse_y);
				
				if(action == GLFW.GLFW_PRESS) {
					mouse_holder = null;
					
					// Make sure we only count the first mouse_press
					if(!isAnyMouseButtonDown()) {
						if(mask != null) {
							mouse_holder = mask.getListener();
						}
						
						has_mouse_holder = true;
					}
				} else if(action == GLFW.GLFW_RELEASE) {
					mouse_buttons[button] = false;
					
					// If this is true we should not remove the holder
					if(!isAnyMouseButtonDown()) {
						mouse_holder = null;
						has_mouse_holder = false;
					}
				}
				
				mouse_buttons[button] = (action != GLFW.GLFW_RELEASE);
				if(mask != null) {
					mask.getListener().onMouseEvent(new GuiMousePress((float)mouse_x, (float)mouse_y, button, action, mods, mask.getData()));
				}
				
				GuiMouseEvent event = new GuiMousePress((float)mouse_x, (float)mouse_y, button, action, mods, null);
				for(GuiListener listener : InputMask.listeners) {
					listener.onMouseEvent(event);
				}
			}
		};
		
		mouse_move = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				Input.mouse_x = xpos;
				Input.mouse_y = ypos;
				
				if(has_mouse_holder) {
					if(mouse_holder != null) {
						mouse_holder.onMouseEvent(new GuiMouseDrag((float)mouse_x, (float)mouse_y, 0, LAST_MODIFIERS, null));
					}
				} else {
					Mask mask = InputMask.getFirstIntersection((float)xpos, (float)ypos);
					if(mask != null) {
						mask.getListener().onMouseEvent(new GuiMouseMove((float)xpos, (float)ypos, LAST_MODIFIERS, mask.getData()));
					}
				}
				
				GuiMouseEvent event;
				if(has_mouse_holder) {
					event = new GuiMouseDrag((float)mouse_x, (float)mouse_y, 0, LAST_MODIFIERS, null);
				} else {
					event = new GuiMouseMove((float)mouse_x, (float)mouse_y, LAST_MODIFIERS, null);
				}
				
				for(GuiListener listener : InputMask.listeners) {
					listener.onMouseEvent(event);
				}
			}
		};
		
		mouse_wheel = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				Input.scroll_delta_x = xoffset;
				Input.scroll_delta_y = yoffset;
				
				GuiMouseScroll event = null;
				
				if(has_mouse_holder) {
					if(mouse_holder != null) {
						mouse_holder.onMouseEvent(event = new GuiMouseScroll((float)mouse_x, (float)mouse_y, (float)yoffset, LAST_MODIFIERS, null));
					}
				} else {
					Mask mask = InputMask.getFirstIntersection((float)mouse_x, (float)mouse_y);
					if(mask != null) {
						mask.getListener().onMouseEvent(event = new GuiMouseScroll((float)mouse_x, (float)mouse_y, (float)yoffset, LAST_MODIFIERS, mask.getData()));
					}
				}
				
				if(event != null && event.isConsumed()) {
					Input.scroll_delta_x = 0;
					Input.scroll_delta_y = 0;
				}
				
				event = new GuiMouseScroll((float)mouse_x, (float)mouse_y, (float)yoffset, LAST_MODIFIERS, null);
				for(GuiListener listener : InputMask.listeners) {
					listener.onMouseEvent(event);
				}
			}
		};
		
		window_focus = new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				Input.has_focus = focused;
				Input.mouse_holder = null;
				Input.has_mouse_holder = false;
			}
		};
	}
	
	private boolean isAnyMouseButtonDown() {
		for(int i = 0, len = mouse_buttons.length; i < len; i++) {
			if(mouse_buttons[i]) return true;
		}
		return false;
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
		InputMask.flush();
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
}
