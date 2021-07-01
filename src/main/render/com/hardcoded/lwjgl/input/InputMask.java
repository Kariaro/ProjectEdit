package com.hardcoded.lwjgl.input;

import java.util.*;

import com.hardcoded.render.gui.GuiListener;

/**
 * <pre>
 * GLOBAL [FocusedListener]
 * GLOBAL [LastMouseListener]
 *
 * fun onEvent(Event):
 *     IF [Event] IS [KeyEvent] THEN
 *         SEND [FocusedListener]
 *
 *     IF [Event] IS [MouseEvent] THEN
 *         SET [MouseListener] TO [getFirstIntersection([Event])]
 *         SEND [MouseListener]
 *
 *         IF NOT [MouseListener] IS [LastMouseListener] THEN
 *             SET [LastMouseListener] TO [MouseListener]
 *         ;
 *     ;
 * ;
 *
 * ; when rendering a listener call
 * InputMask.addEventMask(x, y, w, h, data, listener);
 *
 * ; this will add the mask to [InputMasks]
 *
 * ; when recieving events from "glfwPollEvents"
 * fun getFirstIntersection(Event):
 *     FOR [Mask] IN REVERSED [InputMasks] DO
 *         IF INTERSECT [Mask] THEN
 *             RETURN [Mask]
 * </pre>
 *
 * @author HardCoded
 */
public class InputMask {
	private InputMask() {}
	
	private static final List<Mask> masks = new ArrayList<>();
	static final Set<GuiListener> listeners = new HashSet<>();
	static GuiListener focusedListener;
	
	protected static class Mask {
		private final float x;
		private final float y;
		private final float w;
		private final float h;
		private final Object data;
		private final GuiListener listener;
		
		public Mask(float x, float y, float w, float h, Object data, GuiListener listener) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.data = data;
			this.listener = listener;
		}
		
		public Object getData() {
			return data;
		}
		
		public GuiListener getListener() {
			return listener;
		}
		
		public boolean contains(float px, float py) {
			return px > x && px <= x + w && py > y && py <= y + h;
		}
		
		@Override
		public String toString() {
			return String.format("Mask { x: %.4f, y: %.4f, width: %.4f, height: %.4f, data: %s, listener: %s }", x, y, w, h, data, listener);
		}
	}
	
	/**
	 * Returns the last mask that contained <b>x</b>, <b>y</b>
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the last mask that contained <b>x</b>, <b>y</b> or {@code null} if it didn't intersect anything
	 */
	protected static Mask getFirstIntersection(float x, float y) {
		final int len = masks.size();
		
		// Check all masks backwards
		for(int i = len - 1; i >= 0; i--) {
			Mask mask = masks.get(i);
			
			if(mask.contains(x, y)) {
				return mask;
			}
		}
		
		return null;
	}
	
	/**
	 * Add a mask to the event listener buss.
	 * If the listener was {@code null} no mask will be added and no action is performed.
	 * If the listener is registered to the global event buss it will not be added.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the mask
	 * @param height the height of the mask
	 * @param listener the listener
	 * 
	 * @see InputMask
	 */
	public static void addEventMask(float x, float y, float width, float height, GuiListener listener) {
		addEventMask(x, y, width, height, null, listener);
	}
	
	/**
	 * Add a mask to the event listener buss.
	 * If the listener was {@code null} no mask will be added and no action is performed.
	 * If the listener is registered to the global event buss it will not be added.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the mask
	 * @param height the height of the mask
	 * @param data extra data
	 * @param listener the listener
	 * 
	 * @see InputMask
	 */
	public static void addEventMask(float x, float y, float width, float height, Object data, GuiListener listener) {
		if(listener == null)
			return;
		
		if(listeners.contains(listener))
			return;
		
		masks.add(new Mask(x, y, width, height, data, listener));
	}
	
	/**
	 * Add a mask to the event listener buss.
	 * If the listener was {@code null} no mask will be added and no action is performed.
	 * If the listener is registered to the global event buss it will not be added.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the mask
	 * @param height the height of the mask
	 * @param data extra data
	 * @param listener the listener
	 * 
	 * @see InputMask
	 */
	public static void addEventMaskLast(float x, float y, float width, float height, Object data, GuiListener listener) {
		if(listener == null)
			return;
		
		if(listeners.contains(listener))
			return;
		
		if(masks.size() == 0) {
			masks.add(new Mask(x, y, width, height, data, listener));
		} else {
			masks.add(0, new Mask(x, y, width, height, data, listener));
		}
		
	}
	
	/**
	 * Register a listener to the global event buss and make it always recieve events.
	 * If the listener was {@code null} no action will be performed.
	 * @param listener the listener
	 */
	public static void registerListener(GuiListener listener) {
		if(listener == null)
			return;
		
		if(listeners.contains(listener))
			return;
		
		listeners.add(listener);
	}
	
	/**
	 * Remove a listener from the global event buss.
	 * @param listener the listener
	 */
	public static void unregisterListener(GuiListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Make the provided listener the currently focused element.
	 * If the listener was {@code null} no action will be performed.
	 * @param listener the listener to focus
	 */
	public static void requestFocus(GuiListener listener) {
		if(listener == null)
			return;
		
		focusedListener = listener;
	}
	
	/**
	 * Returns {@code true} if the specified listener is the current focus holder.
	 * @param listener the listener
	 */
	public static boolean isFocusHolder(GuiListener listener) {
		return focusedListener == listener;
	}
	
	/**
	 * Remove the listener that is currently focused.
	 */
	public static void unsetFocus() {
		focusedListener = null;
	}
	
	/**
	 * Flush internal buffers
	 */
	public static void flush() {
		masks.clear();
	}
	
	/**
	 * Remove all data inside all internal buffers
	 */
	public static void unregisterAll() {
		focusedListener = null;
		listeners.clear();
		masks.clear();
	}
}
