package com.hardcoded.render;

import com.hardcoded.lwjgl.mesh.DynamicMeshBuffer;
import com.hardcoded.lwjgl.shader.Shaders;

public class ShapeRender {
	private static final DynamicMeshBuffer buffer = new DynamicMeshBuffer();
	
	public static void drawDebugTextureRect(float x1, float z1, float x2, float z2, float yLevel) {
		RenderUtil.setShader(Shaders::getPositionUvShader);
		RenderUtil.setColor4f(1, 1, 1, 1);
		
		buffer.reset();
		buffer.uv(
			0, 0,
			1, 0,
			1, 1,
			
			0, 0,
			1, 1,
			0, 1
		);
		
		buffer.pos(
			x1, yLevel, z1,
			x2, yLevel, z1,
			x2, yLevel, z2,
			
			x1, yLevel, z1,
			x2, yLevel, z2,
			x1, yLevel, z2
		);
		
		buffer.render();
	}
	
	public static void drawRect(float x, float y, float w, int h, float zDepth, float r, float g, float b, float a) {
		RenderUtil.setShader(Shaders::getPositionShader);
		RenderUtil.setColor4f(r, g, b, a);
		
		buffer.reset();
		buffer.pos(x, y, zDepth);
		buffer.pos(x+w, y, zDepth);
		buffer.pos(x+w, y+h, zDepth);
		
		buffer.pos(x, y, zDepth);
		buffer.pos(x+w, y+h, zDepth);
		buffer.pos(x, y+h, zDepth);
		buffer.render();
	}

	public static void drawTextureRect(float x, float y, float w, float h, float zDepth, float uv_x1, float uv_y1, float uv_x2, float uv_y2, float r, float g, float b, float a) {
		RenderUtil.setShader(Shaders::getPositionUvShader);
		RenderUtil.setColor4f(r, g, b, a);
		
		buffer.reset();
		buffer.uv(
			uv_x1, uv_y1,
			uv_x2, uv_y1,
			uv_x2, uv_y2,
			
			uv_x1, uv_y1,
			uv_x2, uv_y2,
			uv_x1, uv_y2
		);
		
		buffer.pos(
			x  , y  , zDepth,
			x+w, y  , zDepth,
			x+w, y+h, zDepth,
			
			x  , y  , zDepth,
			x+w, y+h, zDepth,
			x  , y+h, zDepth
		);
		buffer.render();
	}
	
}
