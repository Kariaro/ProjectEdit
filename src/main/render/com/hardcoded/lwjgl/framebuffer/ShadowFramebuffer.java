package com.hardcoded.lwjgl.framebuffer;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.*;

public class ShadowFramebuffer extends Framebuffer {
	private int shadowMap;

	public ShadowFramebuffer(int width, int height) {
		super(width, height);
	}
	
	@Override
	protected void createAttachments() {
		shadowMap = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMap);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL31.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL31.GL_CLAMP_TO_BORDER);
		GL11.glTexParameterfv(GL11.GL_TEXTURE_2D, GL31.GL_TEXTURE_BORDER_COLOR, new float[] { 1, 1, 1, 1 });
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, shadowMap, 0);
	}
	
	@Override
	protected void selfCleanup() {
		GL11.glDeleteTextures(shadowMap);
		shadowMap = 0;
	}
	
	@Override
	protected void selfBind() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	@Override
	protected void selfUnbind() {
		
	}
	
	@Override
	protected void selfResize(int width, int height) {
		// GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer)null);
	}
	
	public int getShadowMap() {
		return shadowMap;
	}
}
