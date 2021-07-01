package com.hardcoded.render;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.hardcoded.render.generator.VersionResourceReader;

public class BiomeBlend {
	private static int[] colorBuffer = new int[65536];
	public BiomeBlend() {
		
	}
	
	public void load(VersionResourceReader reader) {
		BufferedImage bi = reader.readBufferedImage("colormap/grass");
		
		BufferedImage scale = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scale.createGraphics();
		g.drawImage(bi, 0, 0, 256, 256, null);
		g.dispose();
		
		scale.getRGB(0, 0, 256, 256, colorBuffer, 0, 256);
	}
	
	public static int get(double temperature, double humidity) {
		if(temperature < 0) temperature = 0;
		if(temperature > 1) temperature = 1;
		if(humidity < 0) humidity = 0;
		if(humidity > 1) humidity = 1;
		
		humidity = humidity * temperature;
		int x = (int)((1 - temperature) * 255);
		int y = (int)((1 - humidity) * 255);
		int k = y << 8 | x;
		return k > colorBuffer.length ? 0xffff00ff : colorBuffer[k];
	}
}
