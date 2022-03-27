package com.hardcoded.render.gl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.constants.Direction;

public class RenderDrawingUtil {
	public static Vector3f getRay(Matrix4f matrix, int width, int height, float x, float y) {
		return matrix.unproject(x, height - y, 1, new int[] { 0, 0, width, height }, new Vector3f());
	}
	
	private static Vector3f scaleAround(Vector3f vec, Vector3f origin, float scale) {
		return vec.sub(origin, new Vector3f()).normalize().mul(scale).add(origin);
	}
	
	public static void drawFrustum(Matrix4f projectionView, Vector3f origin, int width, int height, float near, float far) {
		Vector3f c000 = scaleAround(getRay(projectionView, width, height, 0, 0), origin, near);
		Vector3f c010 = scaleAround(getRay(projectionView, width, height, 0, height), origin, near);
		Vector3f c100 = scaleAround(getRay(projectionView, width, height, width, height), origin, near);
		Vector3f c110 = scaleAround(getRay(projectionView, width, height, width, 0), origin, near);
		
		Vector3f c001 = scaleAround(c000, origin, far);
		Vector3f c011 = scaleAround(c010, origin, far);
		Vector3f c101 = scaleAround(c100, origin, far);
		Vector3f c111 = scaleAround(c110, origin, far);
		
		int d = 30;
		GL11.glPointSize(10.0f);
		GL11.glBegin(GL11.GL_POINTS);
			for(int i = 0; i <= d; i++) {
				for(int j = 0; j <= d; j++) {
					float rx = (i / (d + 0.0f)) * width;
					float ry = (j / (d + 0.0f)) * height;
					float p = (i + j * d) / (d * d + 0.0f);
					
					Vector3f ray = scaleAround(getRay(projectionView, width, height, rx, ry), origin, far);
					
					GL11.glColor3f(p, p, p);
					GL11.glVertex3f(ray.x, ray.y, ray.z);
				}
			}
		GL11.glEnd();
		
		GL11.glLineWidth(5);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(c000.x, c000.y, c000.z); GL11.glVertex3f(c010.x, c010.y, c010.z);
			GL11.glVertex3f(c010.x, c010.y, c010.z); GL11.glVertex3f(c100.x, c100.y, c100.z);
			GL11.glVertex3f(c100.x, c100.y, c100.z); GL11.glVertex3f(c110.x, c110.y, c110.z);
			GL11.glVertex3f(c110.x, c110.y, c110.z); GL11.glVertex3f(c000.x, c000.y, c000.z);

			GL11.glVertex3f(c000.x, c000.y, c000.z); GL11.glVertex3f(c001.x, c001.y, c001.z);
			GL11.glVertex3f(c010.x, c010.y, c010.z); GL11.glVertex3f(c011.x, c011.y, c011.z);
			GL11.glVertex3f(c100.x, c100.y, c100.z); GL11.glVertex3f(c101.x, c101.y, c101.z);
			GL11.glVertex3f(c110.x, c110.y, c110.z); GL11.glVertex3f(c111.x, c111.y, c111.z);
			
			GL11.glVertex3f(c001.x, c001.y, c001.z); GL11.glVertex3f(c011.x, c011.y, c011.z);
			GL11.glVertex3f(c011.x, c011.y, c011.z); GL11.glVertex3f(c101.x, c101.y, c101.z);
			GL11.glVertex3f(c101.x, c101.y, c101.z); GL11.glVertex3f(c111.x, c111.y, c111.z);
			GL11.glVertex3f(c111.x, c111.y, c111.z); GL11.glVertex3f(c001.x, c001.y, c001.z);
		GL11.glEnd();
	}
	
	public static void drawWireBlock(float x, float y, float z, float xs, float ys, float zs) {
		float x1 = x;
		float y1 = y;
		float z1 = z;
		float x2 = x + xs;
		float y2 = y + ys;
		float z2 = z + zs;
		
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(x1, y1, z1); GL11.glVertex3f(x1, y2, z1);
			GL11.glVertex3f(x1, y2, z1); GL11.glVertex3f(x2, y2, z1);
			GL11.glVertex3f(x2, y2, z1); GL11.glVertex3f(x2, y1, z1);
			GL11.glVertex3f(x2, y1, z1); GL11.glVertex3f(x1, y1, z1);
			
			GL11.glVertex3f(x1, y1, z2); GL11.glVertex3f(x1, y2, z2);
			GL11.glVertex3f(x1, y2, z2); GL11.glVertex3f(x2, y2, z2);
			GL11.glVertex3f(x2, y2, z2); GL11.glVertex3f(x2, y1, z2);
			GL11.glVertex3f(x2, y1, z2); GL11.glVertex3f(x1, y1, z2);
			
			GL11.glVertex3f(x1, y1, z1); GL11.glVertex3f(x1, y1, z2);
			GL11.glVertex3f(x1, y2, z1); GL11.glVertex3f(x1, y2, z2);
			GL11.glVertex3f(x2, y1, z1); GL11.glVertex3f(x2, y1, z2);
			GL11.glVertex3f(x2, y2, z1); GL11.glVertex3f(x2, y2, z2);
		GL11.glEnd();
	}
	
	public static void drawBlock(float x, float y, float z, float xs, float ys, float zs) {
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex3f(x     , y + ys, z);
			GL11.glVertex3f(x + xs, y + ys, z);
			GL11.glVertex3f(x + xs, y     , z);
			GL11.glVertex3f(x     , y     , z);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex3f(x     , y     , z + zs);
			GL11.glVertex3f(x + xs, y     , z + zs);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x     , y + ys, z + zs);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex3f(x + xs, y + ys, z     );
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x + xs, y     , z + zs);
			GL11.glVertex3f(x + xs, y     , z     );
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex3f(x, y + ys, z + zs);
			GL11.glVertex3f(x, y + ys, z     );
			GL11.glVertex3f(x, y     , z     );
			GL11.glVertex3f(x, y     , z + zs);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex3f(x     , y + ys, z     );
			GL11.glVertex3f(x     , y + ys, z + zs);
			GL11.glVertex3f(x + xs, y + ys, z + zs);
			GL11.glVertex3f(x + xs, y + ys, z     );
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glVertex3f(x     , y, z + zs);
			GL11.glVertex3f(x     , y, z     );
			GL11.glVertex3f(x + xs, y, z     );
			GL11.glVertex3f(x + xs, y, z + zs);
		GL11.glEnd();
	}
	
	public static void renderBlock(float x, float y, float z, Vector4f col) {
		renderBlock(x, y, z, col.x, col.y, col.z, col.w, 1, 1, 1, -1);
	}
	
	public static void renderBlock(float x, float y, float z, float rc, float gc, float bc, float ac, int faces) {
		renderBlock(x, y, z, rc, gc, bc, ac, 1, 1, 1, faces);
	}
	
	public static void renderBlock(float x, float y, float z, float rc, float gc, float bc, float ac, float xs, float ys, float zs, int faces) {
		float d = -0.1f;
		
		if((faces & Direction.FACE_BACK) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc, bc, ac);
				GL11.glVertex3f(x     , y + ys, z);
				GL11.glColor4f(rc, gc + d, bc + d, ac);
				GL11.glVertex3f(x + xs, y + ys, z);
				GL11.glVertex3f(x + xs, y     , z);
				GL11.glVertex3f(x     , y     , z);
			GL11.glEnd();
		}
		
		if((faces & Direction.FACE_FRONT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc, bc + d, ac);
				GL11.glVertex3f(x     , y     , z + zs);
				GL11.glColor4f(rc + d, gc + d, bc, ac);
				GL11.glVertex3f(x + xs, y     , z + zs);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x     , y + ys, z + zs);
			GL11.glEnd();
		}
		
		if((faces & Direction.FACE_RIGHT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc + d, bc, ac);
				GL11.glVertex3f(x + xs, y + ys, z     );
				GL11.glColor4f(rc + d, gc, bc + d, ac);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x + xs, y     , z + zs);
				GL11.glVertex3f(x + xs, y     , z     );
			GL11.glEnd();
		}
		
		if((faces & Direction.FACE_LEFT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc + d, bc, ac);
				GL11.glVertex3f(x, y + ys, z + zs);
				GL11.glColor4f(rc, gc, bc + d, ac);
				GL11.glVertex3f(x, y + ys, z     );
				GL11.glVertex3f(x, y     , z     );
				GL11.glVertex3f(x, y     , z + zs);
			GL11.glEnd();
		}
		
		if((faces & Direction.FACE_UP) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc, bc + d, ac);
				GL11.glVertex3f(x     , y + ys, z     );
				GL11.glColor4f(rc, gc + d, bc, ac);
				GL11.glVertex3f(x     , y + ys, z + zs);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x + xs, y + ys, z     );
			GL11.glEnd();
		}
		
		if((faces & Direction.FACE_DOWN) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc + d, bc + d, ac);
				GL11.glVertex3f(x     , y, z + zs);
				GL11.glColor4f(rc + d, gc, bc, ac);
				GL11.glVertex3f(x     , y, z     );
				GL11.glVertex3f(x + xs, y, z     );
				GL11.glVertex3f(x + xs, y, z + zs);
			GL11.glEnd();
		}
	}
}
