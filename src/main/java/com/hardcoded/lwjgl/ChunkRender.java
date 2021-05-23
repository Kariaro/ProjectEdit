package com.hardcoded.lwjgl;

import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.files.IChunk;
import com.hardcoded.mc.general.world.IBlockState;
import com.hardcoded.mc.general.world.World;

public class ChunkRender {
	private static final int FACE_UP = 1;
	private static final int FACE_DOWN = 2;
	private static final int FACE_LEFT = 4;
	private static final int FACE_RIGHT = 8;
	private static final int FACE_FRONT= 16;
	private static final int FACE_BACK = 32;
	
//	private void render_cube(float x, float y, float z, float xs, float ys, float zs, int rgba, int faces) {
//		float rc, gc, bc, ac;
//		{
//			ac = ((rgba >> 24) & 0xff) / 255.0f;
//			rc = ((rgba >> 16) & 0xff) / 255.0f;
//			gc = ((rgba >>  8) & 0xff) / 255.0f;
//			bc = ((rgba      ) & 0xff) / 255.0f;
//		}
//		
//		float d =  - 0.1f;
//		x -= 0.5f;
//		y -= 0.5f;
//		z -= 0.5f;
//		if((faces & FACE_BACK) != 0) {
//			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//				GL11.glColor4f(rc + d, gc, bc, ac);
//				GL11.glVertex3f(x     , y + ys, z);
//				GL11.glColor4f(rc, gc + d, bc + d, ac);
//				GL11.glVertex3f(x + xs, y + ys, z);
//				GL11.glVertex3f(x + xs, y     , z);
//				GL11.glVertex3f(x     , y     , z);
//			GL11.glEnd();
//		}
//		
//		if((faces & FACE_FRONT) != 0) {
//			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//				GL11.glColor4f(rc, gc, bc + d, ac);
//				GL11.glVertex3f(x     , y     , z + zs);
//				GL11.glColor4f(rc + d, gc + d, bc, ac);
//				GL11.glVertex3f(x + xs, y     , z + zs);
//				GL11.glVertex3f(x + xs, y + ys, z + zs);
//				GL11.glVertex3f(x     , y + ys, z + zs);
//			GL11.glEnd();
//		}
//		
//		if((faces & FACE_RIGHT) != 0) {
//			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//				GL11.glColor4f(rc, gc + d, bc, ac);
//				GL11.glVertex3f(x + xs, y + ys, z     );
//				GL11.glColor4f(rc + d, gc, bc + d, ac);
//				GL11.glVertex3f(x + xs, y + ys, z + zs);
//				GL11.glVertex3f(x + xs, y     , z + zs);
//				GL11.glVertex3f(x + xs, y     , z     );
//			GL11.glEnd();
//		}
//		
//		if((faces & FACE_LEFT) != 0) {
//			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//				GL11.glColor4f(rc + d, gc + d, bc, ac);
//				GL11.glVertex3f(x, y + ys, z + zs);
//				GL11.glColor4f(rc, gc, bc + d, ac);
//				GL11.glVertex3f(x, y + ys, z     );
//				GL11.glVertex3f(x, y     , z     );
//				GL11.glVertex3f(x, y     , z + zs);
//			GL11.glEnd();
//		}
//		
//		if((faces & FACE_UP) != 0) {
//			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//				GL11.glColor4f(rc + d, gc, bc + d, ac);
//				GL11.glVertex3f(x     , y + ys, z     );
//				GL11.glColor4f(rc, gc + d, bc, ac);
//				GL11.glVertex3f(x     , y + ys, z + zs);
//				GL11.glVertex3f(x + xs, y + ys, z + zs);
//				GL11.glVertex3f(x + xs, y + ys, z     );
//			GL11.glEnd();
//		}
//		
//		if((faces & FACE_DOWN) != 0) {
//			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
//				GL11.glColor4f(rc, gc + d, bc + d, ac);
//				GL11.glVertex3f(x     , y, z + zs);
//				GL11.glColor4f(rc + d, gc, bc, ac);
//				GL11.glVertex3f(x     , y, z     );
//				GL11.glVertex3f(x + xs, y, z     );
//				GL11.glVertex3f(x + xs, y, z + zs);
//			GL11.glEnd();
//		}
//	}
	
	private static final int COLOR_HASH = 0x30297845;
	private void render_cube_2(IBlockState state, float x, float y, float z, int faces) {
		int rgba = state.getBlockId() * COLOR_HASH;
		rgba = state.getMapColor();
		
		float rc, gc, bc, ac;
		{
			ac = ((rgba >> 24) & 0xff) / 255.0f;
			rc = ((rgba >> 16) & 0xff) / 255.0f;
			gc = ((rgba >>  8) & 0xff) / 255.0f;
			bc = ((rgba      ) & 0xff) / 255.0f;
		}
		
		float d =  - 0.1f;
		float xs = 1;
		float ys = 1;
		float zs = 1;
		x -= 0.5f;
		y -= 0.5f;
		z -= 0.5f;
		if((faces & FACE_BACK) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc, bc, ac);
				GL11.glVertex3f(x     , y + ys, z);
				GL11.glColor4f(rc, gc + d, bc + d, ac);
				GL11.glVertex3f(x + xs, y + ys, z);
				GL11.glVertex3f(x + xs, y     , z);
				GL11.glVertex3f(x     , y     , z);
			GL11.glEnd();
		}
		
		if((faces & FACE_FRONT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc, bc + d, ac);
				GL11.glVertex3f(x     , y     , z + zs);
				GL11.glColor4f(rc + d, gc + d, bc, ac);
				GL11.glVertex3f(x + xs, y     , z + zs);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x     , y + ys, z + zs);
			GL11.glEnd();
		}
		
		if((faces & FACE_RIGHT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc + d, bc, ac);
				GL11.glVertex3f(x + xs, y + ys, z     );
				GL11.glColor4f(rc + d, gc, bc + d, ac);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x + xs, y     , z + zs);
				GL11.glVertex3f(x + xs, y     , z     );
			GL11.glEnd();
		}
		
		if((faces & FACE_LEFT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc + d, bc, ac);
				GL11.glVertex3f(x, y + ys, z + zs);
				GL11.glColor4f(rc, gc, bc + d, ac);
				GL11.glVertex3f(x, y + ys, z     );
				GL11.glVertex3f(x, y     , z     );
				GL11.glVertex3f(x, y     , z + zs);
			GL11.glEnd();
		}
		
		if((faces & FACE_UP) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc, bc + d, ac);
				GL11.glVertex3f(x     , y + ys, z     );
				GL11.glColor4f(rc, gc + d, bc, ac);
				GL11.glVertex3f(x     , y + ys, z + zs);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x + xs, y + ys, z     );
			GL11.glEnd();
		}
		
		if((faces & FACE_DOWN) != 0) {
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
	
//	public void renderSubChunk(int cx, int cy, int cz, SubChunk sub) {
//		int hash = 0x30297845;
//		for(int i = 0; i < 4096; i++) {
//			int x = (i & 15);
//			int z = (i / 16) & 15;
//			int y = i / 256;
//			
//			//if(id == Blocks.AIR || id == 0) continue;
//			
//			IBlockState state = sub.getBlock(x, y, z);
//			
//			int id = sub.data[i];
//			if(sub.isAir(x, y, z)) continue;
//			int col = hash * (id + 0);
//			
//			int flags =
//				((sub.isAir(x + 1, y, z)) ? FACE_RIGHT:0) |
//				((sub.isAir(x - 1, y, z)) ? FACE_LEFT:0) |
//				((sub.isAir(x, y + 1, z)) ? FACE_UP:0) |
//				((sub.isAir(x, y - 1, z)) ? FACE_DOWN:0) |
//				((sub.isAir(x, y, z + 1)) ? FACE_FRONT:0) |
//				((sub.isAir(x, y, z - 1)) ? FACE_BACK:0);
//			
//			if(flags != 0) {
//				render_cube(cx + x, cy + y, cz + z, 1, 1, 1, col, flags);
//			}
//		}
//	}
//	
//	public void renderChunk(int x, int z, RegionChunk chunk) {
//		for(int y = 0; y < 16; y++) {
//			SubChunk sub = chunk.getSubChunk(y);
//			if(sub == null) continue;
//			renderSubChunk(x, y * 16, z, sub);
//		}
//	}
//	
//	public void renderRegionFile(int rx, int rz, RegionFile region) {
//		for(int x = 0; x < 16; x++) {
//			for(int z = 0; z < 16; z++) {
//				if(region.hasChunk(x, z)) {
//					ByteBuf buf = region.getChunkBuffer(x, z);
//					
//					if(buf != null) {
//						renderChunk(rx + x * 16, rz + z * 16, new RegionChunk(buf));
//					}
//				} else {
//					render_cube(rx + x * 16, 0, rz + z * 16, 16, 1, 16, 0x111111);
//				}
//			}
//		}
//	}
	
	private static int getShownFaces(World world, int x, int y, int z) {
		return (world.getBlock(x + 1, y    , z    ).isAir() ? FACE_RIGHT:0)
			 | (world.getBlock(x - 1, y    , z    ).isAir() ? FACE_LEFT:0)
			 | (world.getBlock(x    , y + 1, z    ).isAir() ? FACE_UP:0)
			 | (world.getBlock(x    , y - 1, z    ).isAir() ? FACE_DOWN:0)
			 | (world.getBlock(x    , y    , z + 1).isAir() ? FACE_FRONT:0)
			 | (world.getBlock(x    , y    , z - 1).isAir() ? FACE_BACK:0);
	}
	
	public void renderWorld(World world, int x, int z, int radius) {
		final int xs = x - radius;
		final int xe = x + radius;
		final int zs = z - radius;
		final int ze = z + radius;
		
		for(int i = xs; i <= xe; i++) {
			for(int j = zs; j <= ze; j++) {
				IChunk chunk = world.getChunk(i, j);
				if(chunk.isLoaded()) {
					renderChunk(world, chunk, i * 16, j * 16);
				}
			}
		}
	}
	
	public void renderChunk(World world, IChunk chunk, int cx, int cz) {
		final int chunk_x = cx;
		final int chunk_z = cz;
		
		for(int i = 0; i < 65536; i++) {
			int x = i & 15;
			int z = (i / 16) & 15;
			int y = (i / 256);
			
			final int world_x = chunk_x + x;
			final int world_y = y;
			final int world_z = chunk_z + z;
			
			IBlockState state = world.getBlock(world_x, world_y, world_z);
			if(state.isAir()) continue;
			
			int flags = getShownFaces(world, world_x, world_y, world_z);
			if(flags != 0) {
				render_cube_2(state, world_x, world_y, world_z, flags);
			}
		}
	}
}
