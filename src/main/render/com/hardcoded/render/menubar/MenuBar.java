package com.hardcoded.render.menubar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import com.hardcoded.api.IResource;
import com.hardcoded.lwjgl.LwjglWindow;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;
import com.hardcoded.lwjgl.input.Input;
import com.hardcoded.lwjgl.input.InputMask;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.render.gl.ShapeRender;
import com.hardcoded.render.gui.GuiListener;
import com.hardcoded.render.gui.GuiListener.GuiEvent.*;
import com.hardcoded.render.menubar.IMenuGroup.IMenuEntry;
import com.hardcoded.render.menubar.MenuGraphics.ButtonTexture;
import com.hardcoded.settings.ProjectSettings;
import com.hardcoded.settings.SettingKey;

/**
 * TODO: Instead of creating a menubar using OpenGL try figure out a way to use the native
 *       windowbar instead. This would make cross platform graphics look better.
 *
 * @author HardCoded
 */
@Deprecated
public class MenuBar extends IResource implements GuiListener {
	private final List<IMenuGroup> groups;
	protected MenuGraphics menuGraphics;
	
	public MenuBar() {
		groups = new ArrayList<>();
		menuGraphics = new MenuGraphics();
		
		InputMask.registerListener(new GuiListener() {
			@Override
			public void onMouseEvent(GuiMouseEvent event) {
				
			}
			
			@Override
			public void onKeyEvent(GuiKeyEvent event) {
				if(event.getAction() != GLFW.GLFW_PRESS) return;
				
				int key = event.getKeyCode();
				int mod = event.getModifiers();
				
				for(IMenuGroup group : groups) {
					for(IMenuItem item : group.list) {
						if(item.key == key && item.modifier == mod) {
							if(item.action != null) {
								item.action.run();
							}
							
							return;
						}
					}
				}
			}
		});
		
		initUI();
	}
	
	private void initUI() {
		IMenuGroup fileMenu = IMenuGroup.of("File",
			IMenuItem.of("Open", () -> {
				try(MemoryStack stack = MemoryStack.stackPush()) {
					PointerBuffer filters = stack.mallocPointer(1);
					filters.put(stack.UTF8("*.dat"));
					filters.flip();

					String currentPath = (String)ProjectSettings.getKeyValue(SettingKey.LastWorldPath);
					String result = TinyFileDialogs.tinyfd_openFileDialog("Open world", currentPath, filters, "Minecraft World", false);
					
					if(result != null) {
						File worldFolder = new File(result).getParentFile();
						ProjectEdit.getInstance().loadWorld(worldFolder);
						ProjectSettings.setKeyValue(SettingKey.LastWorldPath, worldFolder.getAbsolutePath());
					}
				}
			}, GLFW.GLFW_KEY_O, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Settings", () -> {
				((ProjectEdit)ProjectEdit.getInstance()).getSettingsWindow().show();
			}, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Save", () -> {
				
			}, GLFW.GLFW_KEY_S, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Reload World", () -> {
				World world = ProjectEdit.getInstance().getWorld();
				if(world != null) {
					ProjectEdit.getInstance().loadWorld(world.getFolder());
				}
			}, GLFW.GLFW_KEY_R, GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT),
			IMenuItem.of("Exit", () -> {
				
			})
		);
		
		IMenuGroup editMenu = IMenuGroup.of("Edit",
			IMenuItem.of("Cut", () -> {
				
			}, GLFW.GLFW_KEY_X, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Copy", () -> {
				
			}, GLFW.GLFW_KEY_C, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Paste", () -> {
				
			}, GLFW.GLFW_KEY_V, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Delete", () -> {
				
			}, GLFW.GLFW_KEY_DELETE),
			IMenuItem.of("Undo", () -> {
				
			}, GLFW.GLFW_KEY_Z, GLFW.GLFW_MOD_CONTROL),
			IMenuItem.of("Redo", () -> {
				
			}, GLFW.GLFW_KEY_Z, GLFW.GLFW_MOD_CONTROL | GLFW.GLFW_MOD_ALT)
		);
		
		IMenuGroup helpMenu = IMenuGroup.of("Help",
			IMenuItem.of("About", () -> {
				
			})
		);
		
		addGroup(fileMenu);
		addGroup(editMenu);
		addGroup(helpMenu);
	}
	
	private void addGroup(IMenuGroup entry) {
		if(entry instanceof IMenuEntry) {
			entry = ((IMenuEntry)entry).build(this, 245);
		}
		groups.add(entry);
	}
	
	@Override
	public void reload() {
		menuGraphics.gui_atlas.reload();
	}
	
	private int selectedMenuIndex = -1;
	private int getItemOffsetX(int index) {
		int result = 0;
		for(int i = 0, len = Math.min(groups.size(), index); i < len; i++) {
			result += groups.get(i).tex.width;
		}
		
		return result;
	}
	
	public boolean isEnbled() {
		return Input.hasFocus()
			&& !LwjglWindow.isMouseCaptured();
	}
	
	@Override
	public void onMouseEvent(GuiMouseEvent event) {
		if(event instanceof GuiMousePress) {
			if(event.getAction() == GLFW.GLFW_PRESS) {
				if(event.getCustomData() instanceof IMenuGroup) {
					return;
				}
				
				if(event.getCustomData() instanceof IMenuItem) {
					IMenuItem item = (IMenuItem)event.getCustomData();
					
					if(item.action != null) {
						item.action.run();
					}
					
					selectedMenuIndex = -1;
					return;
				}
			}
			
			selectedMenuIndex = -1;
		}
		
		if(event instanceof GuiMouseMove
		|| event instanceof GuiMouseDrag
		|| event instanceof GuiMousePress) {
			boolean mouse_down = !(event instanceof GuiMouseMove);
			
			int xp = 0;
			for(int i = 0, len = groups.size(); i < len; i++) {
				IMenuGroup group = groups.get(i);
				
				ButtonTexture tex = group.tex;
				boolean hover = isInside(xp, 0, tex.width, 18);
				boolean press = hover && mouse_down;
				
				if(!isEnbled()) {
					selectedMenuIndex = -1;
				} else {
					if(selectedMenuIndex != -1) {
						press = hover;
					}
					
					if(press || selectedMenuIndex == i) {
						selectedMenuIndex = i;
					}
				}
				
				xp += tex.width;
			}
		}
	}

	@Override
	public void onKeyEvent(GuiKeyEvent event) {
		
	}
	
	public void render() {
		if(InputMask.isFocusHolder(this)) {
			InputMask.unsetFocus();
		}
		
		if(!LwjglWindow.isMouseCaptured()) {
			if(selectedMenuIndex != -1) {
				InputMask.addEventMask(0, 0, LwjglWindow.getWidth(), LwjglWindow.getHeight(), this);
				InputMask.requestFocus(this);
			}
			
			InputMask.addEventMask(0, 0, LwjglWindow.getWidth(), 18, this);
		}
		
		if(!isEnbled()) {
			selectedMenuIndex = -1;
		}
		
		menuGraphics.bind();
		drawMenuBar();
		
		int sel = selectedMenuIndex;
		if(isEnbled()) {
			if(sel >= 0 && sel < groups.size()) {
				IMenuGroup selected = groups.get(sel);
				
				int x = getItemOffsetX(sel);
				drawPopupMenu(selected, x, 18);
			}
		}
		
		menuGraphics.unbind();
	}
	
	public void drawPopupMenu(IMenuGroup menu, int x, int y) {
		if(menu.list.isEmpty()) return;
		
		int pw = menu.getPopupWidth();
		int ph = menu.getPopupHeight() + 2;
		InputMask.addEventMask(x, y, pw, ph, menu, this);
		
		fillRect(x, y, pw, ph, MenuGraphics.POPUP_MENU_OUTLINE);
		fillRect(x+1, y+1, pw - 2, ph - 2, MenuGraphics.POPUP_MENU_FILL);
		fillRect(x+3, y+3, 28, ph-6, MenuGraphics.POPUP_NORMAL_EXTRA);
		
		// (35, 3)
		float xp = x + 3;
		float yp = y + 3;
		for(IMenuItem item : menu.list) {
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
			} else if(hover) {
				uv = tex.get(MenuGraphics.TYPE_HOVER);
			} else {
				uv = tex.get(MenuGraphics.TYPE_NORMAL);
			}
			
			InputMask.addEventMask(xp, yp, w, h, item, this);
			drawUvRect(uv, xp, yp, w, h);
			
			yp += 22;
		}
	}
	
	private void drawMenuBar() {
		fillRect(0, 0, LwjglWindow.getWidth(), 19, 0xcccccc);
		fillRect(0, 0, LwjglWindow.getWidth(), 18, 0xffffff);
		
		float xp = 0;
		float yp = 0;
		
		for(int i = 0, len = groups.size(); i < len; i++) {
			IMenuGroup group = groups.get(i);
			ButtonTexture tex = group.tex;
			
			boolean hover = isInside(xp, 0, tex.width, 18);
			
			float w = tex.width;
			float h = 18;
			AtlasUv uv;
			
			if(!isEnbled()) {
				uv = tex.get(MenuGraphics.TYPE_DISABLED);
			} else {
				if(selectedMenuIndex == i) {
					uv = tex.get(MenuGraphics.TYPE_PRESS);
				} else if(hover) {
					uv = tex.get(MenuGraphics.TYPE_HOVER);
				} else {
					uv = tex.get(MenuGraphics.TYPE_NORMAL);
				}
			}
			

			drawUvRect(uv, xp, yp, w, h);
			
			xp += tex.width;
		}
	}
	
	private void drawUvRect(AtlasUv uv, float xp, float yp, float w, float h) {
		ShapeRender.drawTextureRect(xp, yp, w, h, 0, uv.x0, uv.y0, uv.x1, uv.y1, 1, 1, 1, 1);
	}
	
	private void fillRect(int x, int y, int w, int h, int rgb) {
		ShapeRender.drawRect(x, y, w, h, 0,
			((rgb >> 16) & 0xff) / 255.0f,
			((rgb >> 8) & 0xff) / 255.0f,
			(rgb & 0xff) / 255.0f,
			1.0f
		);
	}
	
	public static boolean isInside(float x, float y, float w, float h) {
		float mx = Input.getMouseX();
		float my = Input.getMouseY();
		return !(mx <= x || my <= y || mx > x + w || my > y + h);
	}
}
