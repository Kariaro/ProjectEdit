package com.hardcoded.lwjgl.data;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.opengl.*;

import com.hardcoded.api.IResource;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;

public class TextureAtlasMipmap extends IResource {
	protected final int width;
	protected final int height;
	
	protected final Set<String> defined_paths;
	protected final TextureAtlas mipmap;
	protected Texture atlas;
	
	public TextureAtlasMipmap(int width, int height) {
		this.width = width;
		this.height = height;
		this.mipmap = new TextureAtlas(0, 16, width, height, true, GL11.GL_NEAREST);
		this.defined_paths = new HashSet<>();
	}
	
	public TextureAtlas getMain() {
		return mipmap;
	}
	
	public int getImageId(String texture) {
		return getMain().getImageId(texture);
	}
	
	public synchronized int addTexture(String path, BufferedImage bi) {
		if(defined_paths.contains(path))
			return getMain().getImageId(path);
		
		defined_paths.add(path);
		return mipmap.addTexture(path, bi);
	}
	
	@Override
	public void unload() {
		if(atlas != null) {
			atlas.cleanup();
			atlas = null;
		}
		
		this.defined_paths.clear();
		this.mipmap.unload();
	}
	
	@Override
	public void reload() {
		if(atlas != null) {
			atlas.cleanup();
			atlas = null;
		}
		
		int textureId = GL11.glGenTextures();
		
		GL20.glActiveTexture(GL20.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 4);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
		
		fixImage(mipmap.image);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mipmap.getWidth(), mipmap.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Texture.loadBuffer(mipmap.image));
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP);
		
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		this.atlas = new Texture(textureId, 0);
	}
	
	private void fixImage(BufferedImage bi) {
		// Correct color around alpha to make image look beter when blending linearly
		
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		BufferedImage copy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = copy.createGraphics();
		int len = 8;
		for(int i = len; i >= 1; i--) {
			g.drawImage(bi, -i, -i, null);
			g.drawImage(bi, -i,  i, null);
			g.drawImage(bi,  i, -i, null);
			g.drawImage(bi,  i,  i, null);
		}
		for(int i = len; i >= 1; i--) {
			g.drawImage(bi,  0, -i, null);
			g.drawImage(bi,  i,  0, null);
			g.drawImage(bi,  0,  i, null);
			g.drawImage(bi,  0,  0, null);
		}
		g.dispose();
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				int alpha = bi.getRGB(x, y) >>> 24;
				if(alpha == 0) {
					bi.setRGB(x, y, copy.getRGB(x, y) & 0xffffff);
				}
			}
		}
	}

	public AtlasUv getUv(int id) {
		return getMain().getUv(id);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Texture getTexture() {
		return atlas;
	}
	
	public int getTextureId() {
		if(atlas == null)
			return 0;
		
		return atlas.textureId;
	}
	
	public void transformModelUv(int id, float[] uvs) {
		AtlasUv uv = getUv(id);
		uv.modify(uvs);
	}
	
	public void bind() {
		if(atlas != null) {
			atlas.bind();
		}
	}
	
	public void unbind() {
		if(atlas != null) {
			atlas.unbind();
		}
	}
	
	public boolean isTranclucent(float[] uv) {
		return getMain().isTranclucent(uv);
	}
}
