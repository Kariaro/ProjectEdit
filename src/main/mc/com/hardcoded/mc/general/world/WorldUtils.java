package com.hardcoded.mc.general.world;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.files.Position;
import com.hardcoded.render.utils.RenderUtil;

public class WorldUtils {
	public static Position raycastBlock_GL(World world, Vector3f camera, Vector3f dir, float range) {
		Vector3f ray = dir.sub(camera, new Vector3f());
		Vector3f cam = camera.get(new Vector3f());
		
		Vector3i cam_block = new Vector3i(
			(int)cam.x - ((cam.x < 0) ? 1:0),
			(int)cam.y - ((cam.y < 0) ? 1:0),
			(int)cam.z - ((cam.z < 0) ? 1:0)
		);
		
		{
			GL11.glColor3f(1.0f, 1.0f, 0.5f);
			for(int i = 0; i < 27; i++) {
				int xo = (i % 3) - 1;
				int yo = ((i / 3) % 3) - 1;
				int zo = (i / 9) - 1;
				
				if(i != 13) continue;
				
				xo += cam_block.x;
				yo += cam_block.y;
				zo += cam_block.z;
				
				RenderUtil.drawWireBlock(xo, yo, zo, 1, 1, 1);
			}
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
			{
				int xss = ray.x > 0 ? 1:0;
				int yss = ray.y > 0 ? 1:0;
				int zss = ray.z > 0 ? 1:0;
				
				GL11.glColor4f(0.4f, 0, 0, 0.4f);
				RenderUtil.drawBlock(cam_block.x + xss, cam_block.y, cam_block.z, 0, 1, 1);
				GL11.glColor4f(0, 0.4f, 0, 0.4f);
				RenderUtil.drawBlock(cam_block.x, cam_block.y + yss, cam_block.z, 1, 0, 1);
				GL11.glColor4f(0, 0, 0.4f, 0.4f);
				RenderUtil.drawBlock(cam_block.x, cam_block.y, cam_block.z + zss, 1, 1, 0);
			}
			GL11.glDisable(GL11.GL_BLEND);
		}
		
		Vector3f dx = ray.div(Math.abs(ray.x), new Vector3f());
		Vector3f dy = ray.div(Math.abs(ray.y), new Vector3f());
		Vector3f dz = ray.div(Math.abs(ray.z), new Vector3f());
		
		float dxl = dx.length();
		float dyl = dy.length();
		float dzl = dz.length();
		float dxs = 0;
		float dys = 0;
		float dzs = 0;
		
		{
			float ttx = ((cam.x % 1.0f) + 1.0f) % 1.0f;
			float tty = ((cam.y % 1.0f) + 1.0f) % 1.0f;
			float ttz = ((cam.z % 1.0f) + 1.0f) % 1.0f;
			
			if(ray.x > 0) ttx = 1 - ttx;
			if(ray.y > 0) tty = 1 - tty;
			if(ray.z > 0) ttz = 1 - ttz;

			dxs = dxl * ttx;
			dys = dyl * tty;
			dzs = dzl * ttz;
			
			Vector3f x_start = dx.mul(ttx, new Vector3f());
			Vector3f y_start = dy.mul(tty, new Vector3f());
			Vector3f z_start = dz.mul(ttz, new Vector3f());
			
			GL11.glPointSize(20);
			GL11.glBegin(GL11.GL_POINTS);
				GL11.glColor3f(1, 1, 0);
				GL11.glVertex3f(x_start.x + cam.x, x_start.y + cam.y, x_start.z + cam.z);
				GL11.glColor3f(1, 0, 1);
				GL11.glVertex3f(y_start.x + cam.x, y_start.y + cam.y, y_start.z + cam.z);
				GL11.glColor3f(0, 1, 1);
				GL11.glVertex3f(z_start.x + cam.x, z_start.y + cam.y, z_start.z + cam.z);
			GL11.glEnd();
			
			GL11.glBegin(GL11.GL_LINES);
				GL11.glColor3f(1, 1, 1);
				GL11.glVertex3f(cam.x, cam.y, cam.z);
				GL11.glVertex3f(dir.x, dir.y, dir.z);
			GL11.glEnd();
		}
		
		// Directions
		int xd = ray.x < 0 ? -1:1;
		int yd = ray.y < 0 ? -1:1;
		int zd = ray.z < 0 ? -1:1;
		
		GL11.glPointSize(10.0f);
		GL11.glLineWidth(4);
		int xi = 0;
		int yi = 0;
		int zi = 0;
		for(int i = 0; i < range; i++) {
			float xp = Math.abs(xi) * dxl + dxs;
			float yp = Math.abs(yi) * dyl + dys;
			float zp = Math.abs(zi) * dzl + dzs;
			
			if(xp < yp) {
				if(xp > zp) {
					zi += zd;
					GL11.glColor3f(0, 0, 1);
				} else {
					xi += xd;
					GL11.glColor3f(1, 0, 0);
				}
			} else {
				if(yp > zp) {
					zi += zd;
					GL11.glColor3f(0, 0, 1);
				} else {
					yi += yd;
					GL11.glColor3f(0, 1, 0);
				}
			}
			
			int bx = cam_block.x + xi;
			int by = cam_block.y + yi;
			int bz = cam_block.z + zi;
			
			RenderUtil.drawWireBlock(bx, by, bz, 1, 1, 1);
		}
		
		return null;
	}
	
	public static Position raycastBlock(World world, Vector3f cam, Vector3f ray, float range) {
		float ray_len = ray.length();
		
		// Delta xyz length
		float dxl = ray_len / Math.abs(ray.x);
		float dyl = ray_len / Math.abs(ray.y);
		float dzl = ray_len / Math.abs(ray.z);
		
		// Delta xyz start
		float dxs = 0;
		float dys = 0;
		float dzs = 0;
		
		{
			float ttx = ((cam.x % 1.0f) + 1.0f) % 1.0f;
			float tty = ((cam.y % 1.0f) + 1.0f) % 1.0f;
			float ttz = ((cam.z % 1.0f) + 1.0f) % 1.0f;
			
			if(ray.x > 0) ttx = 1 - ttx;
			if(ray.y > 0) tty = 1 - tty;
			if(ray.z > 0) ttz = 1 - ttz;

			dxs = dxl * ttx;
			dys = dyl * tty;
			dzs = dzl * ttz;
		}
		
		// Directions
		int xd = ray.x < 0 ? -1:1;
		int yd = ray.y < 0 ? -1:1;
		int zd = ray.z < 0 ? -1:1;
		
		// Block start
		int bx = (int)cam.x - ((cam.x < 0) ? 1:0);
		int by = (int)cam.y - ((cam.y < 0) ? 1:0);
		int bz = (int)cam.z - ((cam.z < 0) ? 1:0);
		
		// Length values
		float xp = dxs;
		float yp = dys;
		float zp = dzs;
		
		for(int i = 0; i < range; i++) {
			if(xp < yp) {
				if(xp > zp) {
					bz += zd;
					zp += dzl;
				} else {
					bx += xd;
					xp += dxl;
				}
			} else {
				if(yp > zp) {
					bz += zd;
					zp += dzl;
				} else {
					by += yd;
					yp += dyl;
				}
			}
			
			IBlockData block = world.getBlock(bx, by, bz);
			
			if(!block.isAir()) {
				return Position.get(bx, by, bz);
			}
		}
		
		return null;
	}
	
//	public static Position raycastBlock(World world, Vector3f cam, Vector3f ray, float range) {
//		Vector3i cam_block = new Vector3i(
//			(int)cam.x - ((cam.x < 0) ? 1:0),
//			(int)cam.y - ((cam.y < 0) ? 1:0),
//			(int)cam.z - ((cam.z < 0) ? 1:0)
//		);
//		
//		// Delta xyz start
//		float dxs = 0;
//		float dys = 0;
//		float dzs = 0;
//		
//		// Delta xyz length
//		float dxl = 0;
//		float dyl = 0;
//		float dzl = 0;
//		
//		{
//			float ray_len = ray.length();
//			dxl = ray_len / Math.abs(ray.x);
//			dyl = ray_len / Math.abs(ray.y);
//			dzl = ray_len / Math.abs(ray.z);
//			
//			float ttx = ((cam.x % 1.0f) + 1.0f) % 1.0f;
//			float tty = ((cam.y % 1.0f) + 1.0f) % 1.0f;
//			float ttz = ((cam.z % 1.0f) + 1.0f) % 1.0f;
//			
//			if(ray.x > 0) ttx = 1 - ttx;
//			if(ray.y > 0) tty = 1 - tty;
//			if(ray.z > 0) ttz = 1 - ttz;
//
//			dxs = dxl * ttx;
//			dys = dyl * tty;
//			dzs = dzl * ttz;
//			
//			GL11.glBegin(GL11.GL_LINES);
//				GL11.glColor3f(1, 1, 1);
//				GL11.glVertex3f(cam.x, cam.y, cam.z);
//				GL11.glVertex3f(cam.x + ray.x, cam.y + ray.y, cam.z + ray.z);
//			GL11.glEnd();
//		}
//		
//		// Directions
//		int xd = ray.x < 0 ? -1:1;
//		int yd = ray.y < 0 ? -1:1;
//		int zd = ray.z < 0 ? -1:1;
//		
//		GL11.glPointSize(10.0f);
//		GL11.glLineWidth(4);
//		int xi = 0;
//		int yi = 0;
//		int zi = 0;
//		for(int i = 0; i < range; i++) {
//			float xp = Math.abs(xi) * dxl + dxs;
//			float yp = Math.abs(yi) * dyl + dys;
//			float zp = Math.abs(zi) * dzl + dzs;
//			
//			if(xp < yp) {
//				if(xp > zp) {
//					zi += zd;
//				} else {
//					xi += xd;
//				}
//			} else {
//				if(yp > zp) {
//					zi += zd;
//				} else {
//					yi += yd;
//				}
//			}
//			
//			int bx = cam_block.x + xi;
//			int by = cam_block.y + yi;
//			int bz = cam_block.z + zi;
//			
//			IBlockData block = world.getBlock(bx, by, bz);
//			
//			if(!block.isAir()) {
//				RenderUtil.drawWireBlock(bx, by, bz, 1, 1, 1);
//				return Position.get(bx, by, bz);
//			}
//		}
//		
//		return null;
//	}
}
