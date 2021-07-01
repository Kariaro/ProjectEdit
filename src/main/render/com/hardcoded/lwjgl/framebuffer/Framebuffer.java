package com.hardcoded.lwjgl.framebuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.hardcoded.lwjgl.LwjglWindow;

public abstract class Framebuffer {
	protected int fbo;
	protected int width;
	protected int height;
	
	public Framebuffer(int width, int height) {
		this.height = height;
		this.width = width;
		
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL11.GL_NONE);
		GL11.glReadBuffer(GL11.GL_NONE);
		createAttachments();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void resize(int width, int height) {
		this.height = height;
		this.width = width;
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		selfResize(width, height);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public final void cleanup() {
		GL30.glDeleteFramebuffers(fbo);
		selfCleanup();
	}
	
	public final void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glViewport(0, 0, width, height);
		selfBind();
	}
	
	public final void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		selfUnbind();
		GL11.glViewport(0, 0, LwjglWindow.getWidth(), LwjglWindow.getHeight());
	}
	
	protected abstract void selfBind();
	protected abstract void selfUnbind();
	protected abstract void selfCleanup();
	protected abstract void selfResize(int width, int height);
	protected abstract void createAttachments();
}
