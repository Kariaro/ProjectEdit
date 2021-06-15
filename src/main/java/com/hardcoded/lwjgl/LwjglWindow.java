package com.hardcoded.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.WGL;
import org.lwjgl.system.windows.User32;

import com.hardcoded.lwjgl.async.LwjglAsyncThread;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.util.LoadingException;
import com.hardcoded.render.LwjglRender;

/**
 * This is the main thread of the lwjgl application.
 */
public class LwjglWindow implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(LwjglWindow.class);
	private static LwjglWindow instance;
			
	public static final BufferedImage ICON = null;
	public static final int TARGET_FPS = 120;
	
	protected final ConcurrentLinkedDeque<Runnable> tasks;
	private LwjglRender render;
	private boolean running;
	private long window;
	private int fps;
	private Input input;
	
	private int width;
	private int height;
	
	private Thread runningThread;
	private Thread asyncThread;
	public LwjglWindow() {
		instance = this;
		tasks = new ConcurrentLinkedDeque<>();
		LwjglSettings.getFov();
	}
	
	public synchronized void start() {
		if(running || (runningThread != null && runningThread.isAlive())) return;
		running = true;
		runningThread = new Thread(this, "Main Thread");
		runningThread.start();
	}
	
	public synchronized void stop() {
		running = false;
		try {
			runningThread.join();
		} catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		runningThread = null;
	}
	
	public int getFps() {
		return fps;
	}
	
	private boolean init() {
		if(!glfwInit()) {
			return false;
		}

		int height = (int)(540 * 1.5);
		int width = (int)(960);
		this.width = width;
		this.height = height;
		
		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		window = glfwCreateWindow(width, height, "ProjectEdit - viewer", NULL, NULL);
		if(window == NULL) {
			throw new LoadingException("Failed to initialize the window: window == NULL");
		}
		
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
		
		input = new Input();
		glfwSetKeyCallback(window, input.getKeyboard());
		glfwSetCursorPosCallback(window, input.getMouse());
		glfwSetMouseButtonCallback(window, input.getMouseButton());
		
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
			public void invoke(long window, int width, int height) {
				render.setViewport(width, height);
				LwjglWindow.this.width = width;
				LwjglWindow.this.height = height;
			}
		});
		
		glfwMakeContextCurrent(window);
//		if(ICON != null) {
//			GLFWImage image = GLFWImage.malloc();
//			GLFWImage.Buffer buffer = GLFWImage.malloc(1);
//			image.set(ICON.getWidth(), ICON.getHeight(), Texture.loadBuffer(ICON));
//			buffer.put(0, image);
//			glfwSetWindowIcon(window, buffer);
//		}
		
		GL.createCapabilities();
		GL.createCapabilitiesWGL();
		
		long hwnd = GLFWNativeWin32.glfwGetWin32Window(window);
		long dc = User32.GetDC(hwnd);
		
		long context_1 = WGL.wglCreateContext(dc);
		long context_2 = WGL.wglCreateContext(dc);
		
		boolean success = true;
		if(!WGL.wglShareLists(context_1, context_2)) {
			LOGGER.error("Failed to create shared list!");
			WGL.wglDeleteContext(context_2);
			success = false;
		}
		
		asyncThread = new Thread(new LwjglAsyncThread(dc, context_2, success), "Async Thread");
		asyncThread.setDaemon(true);
		asyncThread.start();
		
		WGL.wglMakeCurrent(dc, context_1);
		render = new LwjglRender(window, width, height);
		glfwShowWindow(window);
		
		return true;
	}
	
	@Override
	public void run() {
		if(!init()) {
			throw new RuntimeException("Failed to initialize the LWJGL window");
		}
		
		double SLEEP_TIME = 1000.0 / (double)TARGET_FPS;
		
		int frames = 0;
		long last = System.currentTimeMillis();
		double next = System.currentTimeMillis() + SLEEP_TIME;
		long last_delta = System.nanoTime();
		try {
			while(running) {
				// Run tasks
				while(!tasks.isEmpty()) {
					tasks.poll().run();
				}
				
				if(Input.pollKey(GLFW.GLFW_KEY_LEFT_ALT) && !Input.isControlDown()) {
					// Capture the mouse or not
					capture_mouse = !capture_mouse;
					
					if(capture_mouse) {
						GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
					} else {
						GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
					}
				}
				
				{
					long now = System.currentTimeMillis();
					if(now < next) {
						Thread.sleep((long)(next - now));
					}
					next += SLEEP_TIME;
					if(now > next + SLEEP_TIME) {
						next += (long)((now - next) / SLEEP_TIME) * SLEEP_TIME;
					}
				}
				
				{
					long now = System.nanoTime();
					delta_time = ((int)(now - last_delta)) / 1000000000.0f;
					last_delta = now;
				}
				
				
				try {
					render.render();
					render.update();
					frames++;
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				long now = System.currentTimeMillis();
				if(now - last > 1000) {
					fps = frames;
					LOGGER.info("fps: {}", fps);
					frames = 0;
					last += 1000;
				}
				
				if(glfwWindowShouldClose(window)) {
					running = false;
				}
			}
		} catch(InterruptedException e) {
			LOGGER.error(e);
		}
		
		render.cleanup();
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	
	private static boolean capture_mouse = false;
	public static boolean isMouseCaptured() {
		return capture_mouse;
	}
	
	private static float delta_time = 0;
	public static float getDeltaTime() {
		return delta_time;
	}
	
	/**
	 * @return {@code true} if the current thread is running on the main thread
	 */
	public static boolean isCurrentThread() {
		return Thread.currentThread() == instance.runningThread;
	}
	
	/**
	 * Run a task on the main thread.
	 * @param runnable a task
	 */
	public static void runLater(Runnable runnable) {
		instance.tasks.add(runnable);
	}
	
	
	public static int getWidth() {
		return instance.width;
	}
	
	public static int getHeight() {
		return instance.height;
	}
}
