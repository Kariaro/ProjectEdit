package com.hardcoded.render.menubar;

import static com.hardcoded.render.menubar.NativeDialog.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.hardcoded.api.IResource;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiKeyEvent;
import com.hardcoded.render.gui.GuiListener.GuiEvent.GuiMouseEvent;
import com.hardcoded.render.menubar.MenuGraphics.ButtonTexture;

public class MenuBar extends IResource implements GuiListener {
	private final List<IMenuItem> items;
	private MenuGraphics testMenu;
	
	public MenuBar() {
		items = new ArrayList<>();
		testMenu = new MenuGraphics();
		
		addItem("File", i -> i
			.add("Open", () -> {
				try(MemoryStack stack = MemoryStack.stackPush()) {
					OPENFILENAMEW ofn = new OPENFILENAMEW();
					
					ByteBuffer lpstrFileTitle;
					ByteBuffer lpstrFile;
					
					ofn.hwndOwner = LwjglWindow.getWindowHwnd();
					ofn.Flags = OFN_DONTADDTORECENT | OFN_FILEMUSTEXIST;
					ofn.lpstrFilter = MemoryUtil.memAddress(stack.UTF16Safe("All Files\0*.*\0Minecraft World\0level.dat\0\0", true));
					ofn.nFilterIndex = 2;
					ofn.lpstrInitialDir = NULL;
					ofn.lpstrFileTitle = MemoryUtil.memAddress(lpstrFileTitle = stack.UTF16Safe("\0".repeat(MAX_PATH), true));
					ofn.nMaxFileTitle = MAX_PATH;
					ofn.lpstrFile = MemoryUtil.memAddress(lpstrFile = stack.UTF16Safe("\0".repeat(MAX_PATH), true));
					ofn.nMaxFile = MAX_PATH;
					
					if(!NativeDialog.GetOpenFileNameW(ofn)) {
						int error = NativeDialog.CommDlgExtendedError();
						System.out.println("CommDlgExtendedError: " + error);
					} else {
						String fileTitle = MemoryUtil.memUTF16(lpstrFileTitle);
						String file = MemoryUtil.memUTF16(lpstrFile);
						System.out.printf("lpstrFileTitle: [%s]\n", fileTitle);
						System.out.printf("lpstrFile: [%s]\n", file);
						
						File worldFolder = new File(file).getParentFile();
						ProjectEdit.getInstance().loadWorld(worldFolder);
					}
				}
			})
			.add("Exit", () -> {
				System.out.println("Exit the application");
			})
		);
		
		addItem("Help", i -> i
			.add("About", () -> {
				System.out.println("Clicked about");
				selectedMenuIndex = -1;
			})
		);
	}
	
	private void addItem(String name, Consumer<IMenuItem> callback) {
		IMenuItem item = new IMenuItem(name);
		callback.accept(item);
		items.add(item);
	}
	
	@Override
	public void reload() {
		testMenu.gui_atlas.reload();
	}
	
	private int selectedMenuIndex = -1;
	private int getItemOffsetX(int index) {
		int result = 0;
		for(int i = 0, len = Math.min(items.size(), index); i < len; i++) {
			result += items.get(i).tex.width;
		}
		
		return result;
	}
	
	public boolean isEnbled() {
		return Input.hasFocus()
			&& !LwjglWindow.isMouseCaptured();
	}
	
	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		
	}

	@Override
	public void onKeyEvent(GuiKeyEvent event) {
		
	}
	
	public void render() {
		GL20.glUseProgram(0);
		
		int last = selectedMenuIndex;
		drawMenuBar();
		
		int sel = selectedMenuIndex;
		if(Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			// Render one more frame to generate the event
			if(last != sel) {
				sel = last;
			}
		}
		
		if(isEnbled()) {
			Input.sendGuiEvents(sel == -1);
			
			if(sel >= 0 && sel < items.size()) {
				IMenuItem selected = items.get(sel);
				
				int x = getItemOffsetX(sel);
				drawPopupMenu(selected, x, 18);
			}
		}
	}
	
	public void drawPopupMenu(IMenuItem menu, int x, int y) {
		if(menu.list.isEmpty()) return;
		
		int pw = menu.getPopupWidth();
		int ph = menu.getPopupHeight();
		setColor(MenuGraphics.POPUP_MENU_OUTLINE);
		fillRect(x, y, pw, ph);
		
		setColor(MenuGraphics.POPUP_MENU_FILL);
		fillRect(x+1, y+1, pw - 2, ph - 2);
		
		setColor(MenuGraphics.POPUP_MENU_FILL_2);
		fillRect(x+3, y+3, 28, ph-6);
		
		// (35, 3)
		float xp = x + 3;
		float yp = y + 3;
		testMenu.bind();
		for(IPopupItem item : menu.list) {
			ButtonTexture tex = item.tex;
			
			boolean hover = isInside(xp, yp, tex.width, 22);
			boolean press = hover && Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1);
			
			AtlasUv uv;
			float w = tex.width;
			float h = 22;
			if(!Input.hasFocus()) {
				uv = tex.get(MenuGraphics.TYPE_DISABLED);
			} else if(press) {
				uv = tex.get(MenuGraphics.TYPE_PRESS);
				
				if(item.action != null) {
					item.action.run();
				}
			} else if(hover) {
				uv = tex.get(MenuGraphics.TYPE_HOVER);
			} else {
				uv = tex.get(MenuGraphics.TYPE_NORMAL);
			}

			GL11.glColor4f(1, 1, 1, 1);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2f(uv.x1, uv.y1); GL11.glVertex2f(xp  , yp  );
				GL11.glTexCoord2f(uv.x2, uv.y1); GL11.glVertex2f(xp+w, yp  );
				GL11.glTexCoord2f(uv.x2, uv.y2); GL11.glVertex2f(xp+w, yp+h);
				GL11.glTexCoord2f(uv.x1, uv.y2); GL11.glVertex2f(xp  , yp+h);
			GL11.glEnd();
			
			yp += 22;
		}
		testMenu.unbind();
	}
	
	private void drawMenuBar() {
		setColor(0xcccccc);
		fillRect(0, 0, LwjglWindow.getWidth(), 19);
		
		setColor(0xffffff);
		fillRect(0, 0, LwjglWindow.getWidth(), 18);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		float xp = 0;
		float yp = 0;
		
		boolean mouse_down = Input.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_1);
		if(mouse_down) {
			selectedMenuIndex = -1;
		}
		
		testMenu.bind();
		for(int i = 0, len = items.size(); i < len; i++) {
			IMenuItem item = items.get(i);
			
			ButtonTexture tex = item.tex;
			
			boolean hover = isInside(xp, 0, tex.width, 18);
			boolean press = hover && mouse_down;
			
			float w = tex.width;
			float h = 18;
			AtlasUv uv;
			
			if(!isEnbled()) {
				uv = tex.get(MenuGraphics.TYPE_DISABLED);
				selectedMenuIndex = -1;
			} else {
				if(selectedMenuIndex != -1) {
					press = hover;
				}
				
				if(press || selectedMenuIndex == i) {
					uv = tex.get(MenuGraphics.TYPE_PRESS);
					selectedMenuIndex = i;
				} else if(hover) {
					uv = tex.get(MenuGraphics.TYPE_HOVER);
				} else {
					uv = tex.get(MenuGraphics.TYPE_NORMAL);
				}
			}
			
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2f(uv.x1, uv.y1); GL11.glVertex2f(xp  , yp  );
				GL11.glTexCoord2f(uv.x2, uv.y1); GL11.glVertex2f(xp+w, yp  );
				GL11.glTexCoord2f(uv.x2, uv.y2); GL11.glVertex2f(xp+w, yp+h);
				GL11.glTexCoord2f(uv.x1, uv.y2); GL11.glVertex2f(xp  , yp+h);
			GL11.glEnd();
			
			xp += tex.width;
		}
		testMenu.unbind();
	}
	
	private void setColor(int rgb) {
		GL11.glColor3f(
			((rgb >> 16) & 0xff) / 255.0f,
			((rgb >> 8) & 0xff) / 255.0f,
			(rgb & 0xff) / 255.0f
		);
	}
	
	public static boolean isInside(float x, float y, float w, float h) {
		float mx = Input.getMouseX();
		float my = Input.getMouseY();
		return !(mx <= x || my <= y || mx > x + w || my > y + h);
	}
	
	private void fillRect(int x, int y, int w, int h) {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex2i(x  , y  );
			GL11.glVertex2i(x+w, y  );
			GL11.glVertex2i(x+w, y+h);
			GL11.glVertex2i(x  , y+h);
		GL11.glEnd();
	}
	
	private class IMenuItem {
		private final List<IPopupItem> list;
		private final ButtonTexture tex;
		
		public IMenuItem(String name) {
			this.tex = testMenu.getMenuButton(name);
			this.list = new ArrayList<>();
		}
		
		public int getPopupWidth() {
			return 245;
		}
		
		public int getPopupHeight() {
			return 6 + list.size() * 22;
		}
		
		public IMenuItem add(String name, Runnable action) {
			list.add(new IPopupItem(name, getPopupWidth(), action));
			return this;
		}
	}
	
	private class IPopupItem {
		private final ButtonTexture tex;
		private final Runnable action;
		
		public IPopupItem(String name, int width, Runnable action) {
			this.tex = testMenu.getPopupButton(name, width);
			this.action = action;
		}
	}
}