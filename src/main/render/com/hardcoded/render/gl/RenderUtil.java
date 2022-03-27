package com.hardcoded.render.gl;

import java.util.function.Supplier;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.hardcoded.lwjgl.data.TextureResource;
import com.hardcoded.lwjgl.shader.Shader;

public class RenderUtil {
	private static Shader shader;
	private static Matrix4f projectionMatrix = new Matrix4f();
	private static Matrix4f translationMatrix = new Matrix4f();
	private static Vector4f color = new Vector4f(1, 1, 1, 1);
	
	public static void setShader(Supplier<Shader> supplier) {
		setShader(supplier.get());
	}
	
	private static void setShader(Shader shader) {
		if(RenderUtil.shader == shader) {
			return;
		}
		
		if(RenderUtil.shader != null) {
			RenderUtil.shader.unbind();
		}
		
		RenderUtil.shader = shader;
		
		if(shader != null) {
			shader.bind();
			shader.setProjectionMatrix(projectionMatrix);
			shader.setTranslationMatrix(translationMatrix);
			shader.setColor4f(color.x, color.y, color.z, color.w);
		}
	}
	
	public static void setColor3f(float r, float g, float b) {
		shader.setColor4f(r, g, b, 1.0f);
		color.set(r, g, b, 1.0f);
	}
	
	public static void setColor4f(float r, float g, float b, float a) {
		shader.setColor4f(r, g, b, a);
		color.set(r, g, b, a);
	}
	
	public static void setProjectionMatrix(Matrix4f matrix) {
		shader.setProjectionMatrix(matrix);
		RenderUtil.projectionMatrix = matrix;
	}
	
	public static void setTranslationMatrix(Matrix4f matrix) {
		shader.setTranslationMatrix(matrix);
		RenderUtil.translationMatrix = matrix;
	}

	public static void bindTexture(int activeId, TextureResource texture) {
		bindTexture(activeId, texture.getTextureId());
	}
	
	public static void bindTexture(int activeId, int textureId) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + activeId);
		GL13.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
	}
	
	public static void enableCullFace() {
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public static void disableCullFace() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public static void enableDepthTest() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public static void disableDepthTest() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static void setDepthFunc(DepthFunc func) {
		GL11.glDepthFunc(func.id);
	}
	
	public static enum DepthFunc {
		NEVER(GL11.GL_NEVER),
		ALWAYS(GL11.GL_ALWAYS),
		LESS(GL11.GL_LESS),
		LEQUAL(GL11.GL_LEQUAL),
		EQUAL(GL11.GL_EQUAL),
		GREATER(GL11.GL_GREATER),
		GEQUAL(GL11.GL_GEQUAL),
		NOTEQUAL(GL11.GL_NOTEQUAL);
		
		final int id;
		private DepthFunc(int id) {
			this.id = id;
		}
	}
}
