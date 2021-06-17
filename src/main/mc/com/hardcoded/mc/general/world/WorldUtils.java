package com.hardcoded.mc.general.world;

import org.joml.Vector3f;

import com.hardcoded.mc.general.files.Position;

public class WorldUtils {
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
}
