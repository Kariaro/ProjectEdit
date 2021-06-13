package com.hardcoded.lwjgl.mesh;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

public class Mesh {
	protected final int vaoId;
	protected final List<Integer> vboIdList;
	private final int vertexCount;
	
	public Mesh(float[] vertexs, float[] uvs) {
		this(
			vertexs,
			uvs,
			createEmptyFloatArray((vertexs.length / 3) * 4, 0.0f)
		);
	}
	
	public Mesh(float[] vertexs, float[] uvs, float[] colors) {
//		if(LwjglAsyncThread.isCurrentThread()) {
//			throw new RuntimeException("Meshes can only be loaded on the main thread");
//		}
		
		FloatBuffer vertBuffer = null;
		FloatBuffer uvBuffer = null;
		FloatBuffer colBuffer = null;
		
		try {
			vertexCount = vertexs.length;
			vboIdList = new ArrayList<>();
			
			vaoId = GL30.glGenVertexArrays();
			GL30.glBindVertexArray(vaoId);
			
			// Position VBO
			int vboId = GL15.glGenBuffers();
			vboIdList.add(vboId);
			vertBuffer = MemoryUtil.memAllocFloat(vertexs.length);
			vertBuffer.put(vertexs).flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
			
			// Texture coordinates VBO
			vboId = GL15.glGenBuffers();
			vboIdList.add(vboId);
			uvBuffer = MemoryUtil.memAllocFloat(uvs.length);
			uvBuffer.put(uvs).flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvBuffer, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
			

			vboId = GL15.glGenBuffers();
			vboIdList.add(vboId);
			colBuffer = MemoryUtil.memAllocFloat(colors.length);
			colBuffer.put(colors).flip();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colBuffer, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, 0, 0);
			
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
	
	public void cleanUp() {
		GL20.glDisableVertexAttribArray(0);
		
		// Delete the VBOs
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		for(int vboId : vboIdList) {
			GL15.glDeleteBuffers(vboId);
		}
		
		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoId);
	}
	
	protected static float[] createEmptyFloatArray(int length, float defaultValue) {
		float[] result = new float[length];
		Arrays.fill(result, defaultValue);
		return result;
	}
}