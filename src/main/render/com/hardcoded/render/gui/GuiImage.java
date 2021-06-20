package com.hardcoded.render.gui;

import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.data.Texture;
import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;

public class GuiImage extends GuiComponent {
	private boolean has_atlas;
	private TextureAtlas atlas;
	private Texture texture;
	private AtlasUv uv;
	
	public GuiImage() {
		
	}
	
	public GuiImage setTexture(TextureAtlas atlas, String path) {
		this.atlas = atlas;
		this.uv = atlas.getUv(atlas.getImageId(path));
		this.has_atlas = true;
		return this;
	}
	
	public GuiImage setTexture(Texture texture) {
		this.texture = texture;
		this.has_atlas = false;
		return this;
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void render() {
		if(has_atlas) {
			if(atlas == null || uv == null) return;
			
			atlas.bind();
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2d(uv.x1, uv.y1); GL11.glVertex2i(x      , y       );
				GL11.glTexCoord2d(uv.x2, uv.y1); GL11.glVertex2i(x+width, y       );
				GL11.glTexCoord2d(uv.x2, uv.y2); GL11.glVertex2i(x+width, y+height);
				GL11.glTexCoord2d(uv.x1, uv.y2); GL11.glVertex2i(x      , y+height);
			GL11.glEnd();
			atlas.unbind();
		} else {
			if(texture == null) return;
			
			texture.bind();
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2d(0, 0); GL11.glVertex2i(x      , y       );
				GL11.glTexCoord2d(1, 0); GL11.glVertex2i(x+width, y       );
				GL11.glTexCoord2d(1, 1); GL11.glVertex2i(x+width, y+height);
				GL11.glTexCoord2d(0, 1); GL11.glVertex2i(x      , y+height);
			GL11.glEnd();
			texture.unbind();
		}
	}
}
