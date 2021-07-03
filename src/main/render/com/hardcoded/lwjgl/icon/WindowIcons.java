package com.hardcoded.lwjgl.icon;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hardcoded.lwjgl.LwjglWindow;

public class WindowIcons {
	public static final int ICON_256 = 0;
	public static final int ICON_128 = 1;
	public static final int ICON_64 = 2;
	public static final int ICON_32 = 3;
	public static final int ICON_16 = 4;
	
	private final BufferedImage[] icons;
	
	public WindowIcons() {
		icons = new BufferedImage[5];
		
		try {
			for(int i = 0; i < 5; i++) {
				int size = 256 >> i;
				icons[i] = ImageIO.read(LwjglWindow.class.getResourceAsStream("/icons/icon_" + size + ".png"));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getIcon(int index) {
		return icons[index];
	}

	public int getSize() {
		return icons.length;
	}
}
