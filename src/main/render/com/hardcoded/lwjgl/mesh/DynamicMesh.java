package com.hardcoded.lwjgl.mesh;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import com.hardcoded.render.util.FloatArray;

public class DynamicMesh {
	private static final float[] EMPTY = new float[0];
	
	protected final int vaoId;
	protected final int vertVbo;
	protected final int uvVbo;
	protected final int colVbo;
	private int vertexCount;
	
	public DynamicMesh() {
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);
		
		// Vertex
		vertVbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertVbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, EMPTY, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
		
		// Uv
		uvVbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, EMPTY, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
		
		// Color
		colVbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colVbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, EMPTY, GL15.GL_DYNAMIC_DRAW);
		GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void upload(FloatArray vert, FloatArray uv, FloatArray col) {
		uploadData(
			vert.toArray(),
			uv.toArray(),
			col.toArray(),
			vert.size() / 3
		);
	}
	
	protected void uploadData(float[] vert, float[] uv, float[] col, int triangles) {
		uploadData(
			vert,
			resizeArray(uv, triangles, 2),
			resizeArray(col, triangles, 4)
		);
	}
	
	protected void uploadData(float[] vert, float[] uv, float[] col) {
		FloatBuffer vertBuffer = null;
		FloatBuffer uvBuffer = null;
		FloatBuffer colBuffer = null;
		
		try {
			vertexCount = vert.length;
			
			// Vertex
			vertBuffer = MemoryUtil.memAllocFloat(vert.length);
			vertBuffer.put(vert).flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertVbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_DYNAMIC_DRAW);
			
			// Uv
			uvBuffer = MemoryUtil.memAllocFloat(uv.length);
			uvBuffer.put(uv).flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvBuffer, GL15.GL_DYNAMIC_DRAW);
			
			// Color
			colBuffer = MemoryUtil.memAllocFloat(col.length);
			colBuffer.put(col).flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colVbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colBuffer, GL15.GL_DYNAMIC_DRAW);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			GL30.glBindVertexArray(0);
		} finally {
			if(vertBuffer != null) {
				MemoryUtil.memFree(vertBuffer);
			}
			
			if(uvBuffer != null) {
				MemoryUtil.memFree(uvBuffer);
			}
			
			if(colBuffer != null) {
				MemoryUtil.memFree(colBuffer);
			}
		}
	}
	
	public int getVaoId() {
		return vaoId;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public void render() {
		if(vertexCount < 1) return;
		
		GL30.glBindVertexArray(vaoId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	protected void cleanup() {
		GL20.glDisableVertexAttribArray(0);
		
		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vertVbo);
		GL15.glDeleteBuffers(uvVbo);
		GL15.glDeleteBuffers(colVbo);
		
		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}
	
	protected static float[] resizeArray(float[] array, int count, int size) {
		final int length = count * size;
		if(array.length == length)
			return array;
		
		float[] fixed = new float[length];
		System.arraycopy(array, 0, fixed, 0, Math.min(array.length, length));
		return fixed;
	}
}