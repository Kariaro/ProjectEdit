package com.hardcoded.lwjgl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.utils.FastModelJsonLoader;
import com.hardcoded.utils.FastModelRenderer;

public class ChunkRender {
//	public static final int FACE_UP = 1;
//	public static final int FACE_DOWN = 2;
//	public static final int FACE_LEFT = 4;
//	public static final int FACE_RIGHT = 8;
//	public static final int FACE_FRONT= 16;
//	public static final int FACE_BACK = 32;
	
//	private static void render_cube_optimized(IBlockData state, float x, float y, float z, int faces) {
//		int rgba = state.getMapColor();
//		float rc, gc, bc, ac;
//		{
//			ac = ((rgba >> 24) & 0xff) / 255.0f;
//			rc = ((rgba >> 16) & 0xff) / 255.0f;
//			gc = ((rgba >>  8) & 0xff) / 255.0f;
//			bc = ((rgba      ) & 0xff) / 255.0f;
//		}
//		
//		{
//			BlockData bs = (BlockData)state;
//			if(!bs.model_objects.isEmpty()) {
//				for(int i = 0; i < bs.model_objects.size(); i++) {
//					GL11.glPushMatrix();
//					//GL11.glColor4f(rc, gc, bc, ac);
//					GL11.glColor4f(1, 1, 1, 1);
//					GL11.glTranslatef(x, y, z);
//					float s = 1 / 16.0f;
//					GL11.glScalef(s, s, s);
//					//bs.model.render(rc, gc, bc, ac, faces);
//					//bs.model.render(1, 1, 1, 1, faces);
//					
//					GL11.glMultMatrixf(bs.model_transform.get(i).get(new float[16]));
//					FastModelRenderer.renderModel(bs.model_objects.get(i), new Vector4f(1, 1, 1, 1), faces);
//					GL11.glPopMatrix();
//				}
//				return;
//			}
//		}
//		
//		float d =  - 0.1f;
//		float xs = 1;
//		float ys = 1;
//		float zs = 1;
//		
////		x -= 0.5f;
////		y -= 0.5f;
////		z -= 0.5f;
//		if((faces & Blocks.FACE_BACK) != 0) {
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
//		if((faces & Blocks.FACE_FRONT) != 0) {
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
//		if((faces & Blocks.FACE_RIGHT) != 0) {
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
//		if((faces & Blocks.FACE_LEFT) != 0) {
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
//		if((faces & Blocks.FACE_UP) != 0) {
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
//		if((faces & Blocks.FACE_DOWN) != 0) {
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
	
	private static void render_cube(IBlockData state, float x, float y, float z, int faces) {
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
			if(!bs.model_objects.isEmpty()) {
				GL11.glColor4f(1, 1, 1, 1);
				FastModelRenderer.renderModelFast(bs, x, y, z, faces);
				
//				for(int i = 0; i < bs.model_objects.size(); i++) {
//					GL11.glPushMatrix();
//					//GL11.glColor4f(rc, gc, bc, 1);
//					GL11.glColor4f(1, 1, 1, 1);
//					GL11.glTranslatef(x, y, z);
//					float s = 1 / 16.0f;
//					GL11.glScalef(s, s, s);
//					GL11.glMultMatrixf(bs.model_transform.get(i).get(new float[16]));
//					FastModelRenderer.renderModel(bs.model_objects.get(i), faces);
//					GL11.glPopMatrix();
//				}
				return;
			}
		}
		
		renderBlock(x, y, z, rc, gc, bc, ac, faces);
	}
	
	public static void renderBlock(float x, float y, float z, Vector4f col) {
		renderBlock(x, y, z, col.x, col.y, col.z, col.w, 1, 1, 1, -1);
	}
	
	public static void renderBlock(float x, float y, float z, float rc, float gc, float bc, float ac, int faces) {
		renderBlock(x, y, z, rc, gc, bc, ac, 1, 1, 1, faces);
	}
	
	public static void renderBlock(float x, float y, float z, float rc, float gc, float bc, float ac, float xs, float ys, float zs, int faces) {
		float d = -0.1f;
		
		if((faces & Blocks.FACE_BACK) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc, bc, ac);
				GL11.glVertex3f(x     , y + ys, z);
				GL11.glColor4f(rc, gc + d, bc + d, ac);
				GL11.glVertex3f(x + xs, y + ys, z);
				GL11.glVertex3f(x + xs, y     , z);
				GL11.glVertex3f(x     , y     , z);
			GL11.glEnd();
		}
		
		if((faces & Blocks.FACE_FRONT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc, bc + d, ac);
				GL11.glVertex3f(x     , y     , z + zs);
				GL11.glColor4f(rc + d, gc + d, bc, ac);
				GL11.glVertex3f(x + xs, y     , z + zs);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x     , y + ys, z + zs);
			GL11.glEnd();
		}
		
		if((faces & Blocks.FACE_RIGHT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc, gc + d, bc, ac);
				GL11.glVertex3f(x + xs, y + ys, z     );
				GL11.glColor4f(rc + d, gc, bc + d, ac);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x + xs, y     , z + zs);
				GL11.glVertex3f(x + xs, y     , z     );
			GL11.glEnd();
		}
		
		if((faces & Blocks.FACE_LEFT) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc + d, bc, ac);
				GL11.glVertex3f(x, y + ys, z + zs);
				GL11.glColor4f(rc, gc, bc + d, ac);
				GL11.glVertex3f(x, y + ys, z     );
				GL11.glVertex3f(x, y     , z     );
				GL11.glVertex3f(x, y     , z + zs);
			GL11.glEnd();
		}
		
		if((faces & Blocks.FACE_UP) != 0) {
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
				GL11.glColor4f(rc + d, gc, bc + d, ac);
				GL11.glVertex3f(x     , y + ys, z     );
				GL11.glColor4f(rc, gc + d, bc, ac);
				GL11.glVertex3f(x     , y + ys, z + zs);
				GL11.glVertex3f(x + xs, y + ys, z + zs);
				GL11.glVertex3f(x + xs, y + ys, z     );
			GL11.glEnd();
		}
		
		if((faces & Blocks.FACE_DOWN) != 0) {
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
		return (world.getBlock(x + 1, y    , z    ).isOpaque() ? Blocks.FACE_RIGHT:0)
			 | (world.getBlock(x - 1, y    , z    ).isOpaque() ? Blocks.FACE_LEFT:0)
			 | (world.getBlock(x    , y + 1, z    ).isOpaque() ? Blocks.FACE_UP:0)
			 | (world.getBlock(x    , y - 1, z    ).isOpaque() ? Blocks.FACE_DOWN:0)
			 | (world.getBlock(x    , y    , z + 1).isOpaque() ? Blocks.FACE_FRONT:0)
			 | (world.getBlock(x    , y    , z - 1).isOpaque() ? Blocks.FACE_BACK:0);
	}
	
//	public void renderWorld(World world, int x, int z, int radius) {
//		final int xs = x - radius;
//		final int xe = x + radius;
//		final int zs = z - radius;
//		final int ze = z + radius;
//		for(int i = xs; i <= xe; i++) {
//			for(int j = zs; j <= ze; j++) {
//				IChunk chunk = world.getChunk(i, j);
//				if(chunk.isLoaded()) {
//					renderChunk(world, chunk, i * 16, j * 16);
//				}
//			}
//		}
//	}

	private ChunkList list;
	public void renderWorld(World world, Camera camera, int radius) {
		if(list == null) {
			list = new ChunkList(world);
		}
		
		list.render(camera, radius);
	}
	
	private static class ChunkBlob {
		private final Position pos;
		private final World world;
		private final Chunk chunk;
		private final ChunkSectionBlob[] sections;
		
		private ChunkBlob(World world, Chunk chunk, int world_x, int world_z) {
			this.world = world;
			this.chunk = chunk;
			this.pos = Position.get(world_x, 0, world_z);
			this.sections = loadSections();
		}
		
		private ChunkSectionBlob[] loadSections() {
			ChunkSectionBlob[] sections = new ChunkSectionBlob[16];
			for(int y = 0; y < 16; y++) {
				sections[y] = new ChunkSectionBlob(world, pos.offset(0, y * 16, 0), chunk.getSection(y));
			}
			
			return sections;
		}
		
		private boolean isDirty = true;
		private int displayList;
		private int displayCount;
		
		public void render() {
			if(isDirty) {
				if(displayList != 0) {
					GL11.glDeleteLists(displayList, displayCount);
				}
				
				this.displayCount = 0;
				for(ChunkSectionBlob section : sections) {
					this.displayCount += section.doesRender() ? 1:0;
				}
				
				if(displayCount == 0) return;
				isDirty = false;
				
				this.displayList = GL11.glGenLists(displayCount);
				for(int y = 0, i = 0; y < 16; y++) {
					ChunkSectionBlob section = sections[y];
					if(section.doesRender()) {
						section.render(displayList + (i++));
					}
				}
			}
			
			int world_x = pos.getBlockX();
			int world_z = pos.getBlockZ();
			for(int y = 0, i = 0; y < 16; y++) {
				ChunkSectionBlob section = sections[y];
				if(section.doesRender()) {
					GL11.glPushMatrix();
					GL11.glTranslated(world_x, y * 16, world_z);
					GL11.glCallList(displayList + (i++));
					GL11.glPopMatrix();
				}
			}
		}
		
		public void unload() {
			if(displayList != 0) {
				GL11.glDeleteLists(displayList, displayCount);
				displayList = 0;
				displayCount = 0;
			}
		}
	}
	
	private static class ChunkSectionBlob {
		private final World world;
		private final IChunkSection section;
		private final Position pos;
		
		public ChunkSectionBlob(World world, Position pos, IChunkSection section) {
			this.world = world;
			this.section = section;
			this.pos = pos;
		}
		
		public boolean doesRender() {
			return section.isLoaded();
		}
		
		public void render(int displayList) {
			if(!doesRender()) return;
			GL11.glNewList(displayList, GL11.GL_COMPILE);
			
			final int bx = pos.getBlockX();
			final int by = pos.getBlockY();
			final int bz = pos.getBlockZ();
			
			for(int i = 0; i < 4096; i++) {
				int x = (i & 15);
				int y = ((i >> 4) & 15);
				int z = (i >> 8);
				
				IBlockData state = section.getBlock(x, y, z);
				if(state.isAir()) continue;
				
				final int wx = bx + x;
				final int wy = by + y;
				final int wz = bz + z;
				int flags = getShownFaces(world, wx, wy, wz);
				if(flags != 0) {
					render_cube(state, x, y, z, flags);
				}
			}
			
			GL11.glEndList();
		}
	}
	
	private static final long timeout = 1000;
	private static final long load_limit = 8;
	
	private static class ChunkList {
		private final Map<Long, ChunkBlob> chunk_map;
		private final Map<ChunkBlob, Long> time_map;
		private final World world;
		
		public ChunkList(World world) {
			chunk_map = new HashMap<>();
			time_map = new HashMap<>();
			this.world = world;
		}
		
		public void render(Camera camera, int radius) {
			long now = System.currentTimeMillis();
			
			int x = Math.floorDiv((int)camera.x, 16);
			int z = Math.floorDiv((int)camera.z, 16);
			final int xs = x - radius;
			final int xe = x + radius;
			final int zs = z - radius;
			final int ze = z + radius;
			
			{
				Iterator<ChunkBlob> iter = time_map.keySet().iterator();
				
				while(iter.hasNext()) {
					ChunkBlob blob = iter.next();
					int bx = blob.chunk.x;
					int bz = blob.chunk.z;
					long idx = ((long)(bx) & 0xffffffffL) | (((long)bz) << 32L);
					
					if(bx < xs || bx > xe || bz < zs || bz > ze) {
						if(now > time_map.get(blob)) {
							iter.remove();
							chunk_map.remove(idx);
							blob.unload();
						}
					} else {
						time_map.put(blob, now + timeout);
					}
				}
			}

			long loaded = load_limit;
			FastModelJsonLoader.atlas.bind();
			//FrustumIntersection intersect = new FrustumIntersection(camera.getViewMatrix());
			for(int i = xs; i <= xe; i++) {
				for(int j = zs; j <= ze; j++) {
					long idx = ((long)(i) & 0xffffffffL) | (((long)j) << 32L);
					
//					if(intersect.testAab(new Vector3f(i * 16, 0, j * 16).sub(camera.getPosition()), new Vector3f(i * 16 + 15, 16, j * 16 + 15).sub(camera.getPosition()))) {
//						continue;
//						//renderBlock(i * 16, 0, j * 16, 1, 1, 1, 1, 16, 16, 16, -1);
//					}
					
					ChunkBlob blob = chunk_map.get(idx);
					if(blob == null) {
						IChunk chunk = world.getChunk(i, j);
						if(!chunk.isLoaded()) continue;
						
						if(loaded-- > 0) {
							blob = new ChunkBlob(world, (Chunk)chunk, i * 16, j * 16);
							time_map.put(blob, now + timeout);
							chunk_map.put(idx, blob);
						} else {
							continue;
						}
					}
					
					//if(intersect.intersectAab(new Vector3f(i - x, 0, j - z), new Vector3f(i + 1 - x, 16, j + 1 - z)) == FrustumIntersection.INSIDE) {
					blob.render();
					//}
				}
			}
			FastModelJsonLoader.atlas.unbind();
		}
	}
}
