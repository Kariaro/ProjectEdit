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
		if(atlas == null)
			return this;
		
		int id = atlas.getImageId(path);
		if(id < 0)
			return this;
		
		this.atlas = atlas;
		this.uv = atlas.getUv(id);
		this.has_atlas = true;
		return this;
	}
	
	public GuiImage setTexture(Texture texture) {
		this.texture = texture;
		this.has_atlas = false;
		return this;
	}
	
	@Override
	public void renderComponent() {
		int width = getWidth();
		int height = getHeight();
		int x = getX();
		int y = getY();
		
		if(has_atlas) {
			if(atlas == null || uv == null) return;
			
			atlas.bind();
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2f(uv.x0, uv.y0); GL11.glVertex2i(x      , y       );
				GL11.glTexCoord2f(uv.x1, uv.y0); GL11.glVertex2i(x+width, y       );
				GL11.glTexCoord2f(uv.x1, uv.y1); GL11.glVertex2i(x+width, y+height);
				GL11.glTexCoord2f(uv.x0, uv.y1); GL11.glVertex2i(x      , y+height);
			GL11.glEnd();
			atlas.unbind();
		} else {
			if(texture == null) return;
			
			texture.bind();
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glTexCoord2f(0, 0); GL11.glVertex2i(x      , y       );
				GL11.glTexCoord2f(1, 0); GL11.glVertex2i(x+width, y       );
				GL11.glTexCoord2f(1, 1); GL11.glVertex2i(x+width, y+height);
				GL11.glTexCoord2f(0, 1); GL11.glVertex2i(x      , y+height);
			GL11.glEnd();
			texture.unbind();
		}
	}
}
