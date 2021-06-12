package com.hardcoded.render;

import java.lang.Math;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.joml.*;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.LwjglRender;
import com.hardcoded.lwjgl.mesh.Mesh;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.BlockData;
import com.hardcoded.mc.general.world.IBlockData;
import com.hardcoded.mc.general.world.World;
import com.hardcoded.render.utils.MeshBuilder;
import com.hardcoded.utils.FastModelRenderer;

public class ChunkRender {
	private static void render_cube(IBlockData state, float x, float y, float z, MeshBuilder builder, int faces) {
		BlockData bs = (BlockData)state;
		if(bs.model_objects.isEmpty()) return;
		FastModelRenderer.Fast.renderModelFast(bs, x, y, z, builder, faces);
		
//		renderBlock(x, y, z, rc, gc, bc, ac, faces);
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

	private ChunkList list;
	public void renderWorld(World world, Camera camera, Matrix4f projectionView, int radius) {
		if(list == null) {
			list = new ChunkList(world);
		}
		
		list.render(camera, projectionView, radius);
	}
	
	@SuppressWarnings("unused")
	private LwjglRender render;
	public ChunkRender(LwjglRender render) {
		this.render = render;
	}
	
	private class ChunkBlob {
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
		
		private MeshBuilder builder;
		public void reload() {
			if(unloaded) return;
			
			// No calls to GL in here
			
			MeshBuilder buffer = new MeshBuilder();
			
			for(int y = 0; y < 16; y++) {
				if(unloaded) return;
				ChunkSectionBlob section = sections[y];
				if(section.doesRender()) {
					section.render(buffer);
				}
			}
			
			if(unloaded) return;
			this.builder = buffer;
			this.isDirty = true;
		}
		
		private boolean isDirty = false;
		private boolean unloaded = false;
		private Mesh mesh;
		public void render() {
			if(isDirty) {
				isDirty = false;
				
//				TimerUtils.begin();
				if(mesh != null) {
					mesh.cleanUp();
					mesh = null;
				}
				
				if(unloaded) return;
				mesh = builder.build();
				
//				double ms = TimerUtils.end() / 1000000.0;
//				System.out.printf("Loading took: %.4f ms, max: (%.1f) load/s\n", ms, 120 / ms);
			}
			
			if(mesh != null) {
				mesh.render();
			}
		}
		
		public void unload() {
			unloaded = true;
			if(mesh != null) {
				mesh.cleanUp();
				mesh = null;
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
		
		public void render(MeshBuilder builder) {
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
					render_cube(state, x + bx, y + by, z + bz, builder, flags);
				}
			}
		}

		public boolean doesRender() {
			return section.isLoaded();
		}
	}
	
	private static final long timeout = 1000;
	private static final long load_limit = 100;
	private static ThreadPoolExecutor exec = (ThreadPoolExecutor)Executors.newFixedThreadPool(7);
	
	private class ChunkList {
		private final Map<Long, ChunkBlob> chunk_map;
		private final Map<ChunkBlob, Long> time_map;
		private final World world;
		
		public ChunkList(World world) {
			chunk_map = new HashMap<>();
			time_map = new HashMap<>();
			this.world = world;
		}
		
		public void render(Camera camera, Matrix4f projectionView, int radius) {
			long now = System.currentTimeMillis();
			
			int x = Math.floorDiv((int)camera.x, 16);
			int z = Math.floorDiv((int)camera.z, 16);
			final int xs = x - radius - 1;
			final int xe = x + radius + 1;
			final int zs = z - radius - 1;
			final int ze = z + radius + 1;
			
			{
				Set<Long> loaded_regions = new HashSet<>();
				Iterator<ChunkBlob> iter = time_map.keySet().iterator();
				
				final int uxs = x - radius - 1;
				final int uxe = x + radius + 1;
				final int uzs = z - radius - 1;
				final int uze = z + radius + 1;
				
				while(iter.hasNext()) {
					ChunkBlob blob = iter.next();
					int bx = blob.chunk.x;
					int bz = blob.chunk.z;
					
					if(bx < uxs || bx > uxe || bz < uzs || bz > uze) {
						if(now > time_map.get(blob)) {
							long idx = ((long)(bx) & 0xffffffffL) | (((long)bz) << 32L);
							iter.remove();
							chunk_map.remove(idx);
							blob.unload();
						} else {
							loaded_regions.add(blob.pos.getRegionIndex());
						}
					} else {
						time_map.put(blob, now + timeout);
						loaded_regions.add(blob.pos.getRegionIndex());
					}
				}
				
				for(int i = uxs; i <= uxe; i++) {
					for(int j = uzs; j <= uze; j++) {
						int region_x = Math.floorDiv(i, 32);
						int region_z = Math.floorDiv(j, 32);
						long idx = ((long)(region_x) & 0xffffffffL) | (((long)region_z) << 32L);
						loaded_regions.add(idx);
					}
				}
				
				world.unloadRegionsNotFound(loaded_regions);
			}
			
			long loaded = load_limit;
			
			int count = 0;
			
			FrustumIntersection intersect = new FrustumIntersection(projectionView);
			for(int i = xs + 1; i < xe; i++) {
				for(int j = zs + 1; j < ze; j++) {
					long idx = ((long)(i) & 0xffffffffL) | (((long)j) << 32L);
					
					if(!intersect.testAab(new Vector3f(i * 16, 0, j * 16), new Vector3f(i * 16 + 16, 256, j * 16 + 16))) {
						continue;
					}
					
					boolean test = true;
					ChunkBlob blob = chunk_map.get(idx);
					if(blob == null && test) {
						IChunk chunk = world.getChunk(i, j);
						if(!chunk.isLoaded()) continue;
						
						if(loaded-- > 0) {
							blob = new ChunkBlob(world, (Chunk)chunk, i * 16, j * 16);
							time_map.put(blob, now + timeout);
							chunk_map.put(idx, blob);
							exec.submit(blob::reload);
						} else {
							continue;
						}
					}
					
					if(blob != null) {
						blob.render();
						count++;
					}
				}
			}
			
//			System.out.println("Chunks: " + count);
		}
	}
}
