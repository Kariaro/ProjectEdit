package com.hardcoded.lwjgl;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.files.IChunk;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.utils.FastModelRenderer;

public class ChunkRender {
	public static final int FACE_UP = 1;
	public static final int FACE_DOWN = 2;
	public static final int FACE_LEFT = 4;
	public static final int FACE_RIGHT = 8;
	public static final int FACE_FRONT= 16;
	public static final int FACE_BACK = 32;
	
	private void render_cube(IBlockData state, float x, float y, float z, int faces) {
		int rgba = state.getMapColor();
		float rc, gc, bc, ac;
		{
			ac = ((rgba >> 24) & 0xff) / 255.0f;
			rc = ((rgba >> 16) & 0xff) / 255.0f;
			gc = ((rgba >>  8) & 0xff) / 255.0f;
			bc = ((rgba      ) & 0xff) / 255.0f;
		}
		
		{
			BlockData bs = (BlockData)state;
			if(bs.model2 != null) {
				GL11.glPushMatrix();
				GL11.glColor4f(rc, gc, bc, ac);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glTranslatef(x, y, z);
				float s = 1 / 16.0f;
				GL11.glScalef(s, s, s);
				//bs.model.render(rc, gc, bc, ac, faces);
				//bs.model.render(1, 1, 1, 1, faces);
				GL11.glMultMatrixf(bs.model2_transform.get(new float[16]));
				FastModelRenderer.renderModel(bs.model2, new Vector4f(1, 1, 1, 1), faces);
				GL11.glPopMatrix();
				return;
			}
		}
		
		float d =  - 0.1f;
		float xs = 1;
		float ys = 1;
		float zs = 1;
//		x -= 0.5f;
//		y -= 0.5f;
//		z -= 0.5f;
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
			
			IBlockData state = world.getBlock(world_x, world_y, world_z);
			if(state.isAir()) continue;
			
			int flags = getShownFaces(world, world_x, world_y, world_z);
			if(flags != 0) {
				render_cube(state, world_x, world_y, world_z, flags);
			}
		}
	}
}
