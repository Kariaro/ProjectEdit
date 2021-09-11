package com.hardcoded.render;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.hardcoded.lwjgl.Camera;
import com.hardcoded.lwjgl.data.TextureAtlas;
import com.hardcoded.lwjgl.data.TextureAtlas.AtlasUv;
import com.hardcoded.lwjgl.mesh.Mesh;
import com.hardcoded.lwjgl.shader.MeshShader;
import com.hardcoded.lwjgl.shader.ShaderObjectImpl;
import com.hardcoded.main.ProjectEdit;
import com.hardcoded.mc.constants.Direction;
import com.hardcoded.mc.general.files.*;
import com.hardcoded.mc.general.world.*;
import com.hardcoded.render.RenderUtil.DepthFunc;
import com.hardcoded.render.generator.FastModelRenderer;
import com.hardcoded.render.generator.MeshBuilder;
import com.hardcoded.render.util.MeshBuffer;
import com.hardcoded.util.math.box.BlockShape;

public class WorldRender {
	private static void renderBlockWithBiome(World world, IBlockData block, Biome biome, Position pos, float x, float y, float z, MeshBuilder builder, int faces) {
		if(block.getBlockId() == Blocks.WATER.getBlockId()) {
			renderLiquid(world, block, biome, pos, x, y, z, builder, faces);
			return;
		}
		
		if(!FastModelRenderer.hasLoadedModel(block)) {
//			FastModelRenderer.renderModelObject(FastModelJsonLoader.getMissingBlockModel(), x, y, z, opaque, faces);
		} else {
			MeshBuffer buffer;
			if(FastModelRenderer.hasTranslucency(block)) {
				buffer = builder.translucent;
			} else {
				buffer = builder.opaque;
			}
			
			FastModelRenderer.renderModelFastBiome(block, biome, x, y, z, buffer, faces);
		}
	}
	
	private static void renderLiquid(World world, IBlockData block, Biome biome, Position pos, float xp, float yp, float zp, MeshBuilder builder, int faces) {
		int x = (int)xp + pos.getBlockX();
		int y = (int)yp;
		int z = (int)zp + pos.getBlockZ();
		
		float y0 = (8 - getWaterHeight(world, (int)x - 1, (int)y, (int)z    )) * 2 - 1.5f;
		float y1 = (8 - getWaterHeight(world, (int)x    , (int)y, (int)z    )) * 2 - 1.5f;
		float y2 = (8 - getWaterHeight(world, (int)x    , (int)y, (int)z - 1)) * 2 - 1.5f;
		float y3 = (8 - getWaterHeight(world, (int)x - 1, (int)y, (int)z - 1)) * 2 - 1.5f;
		float[] top = {
			 0, y0, 16, // 0
			16, y1, 16, // 1
			16, y2,  0, // 2
			
			 0, y0, 16, // 0
			16, y2,  0, // 2
			 0, y3,  0, // 3
			 
			16, y2,  0, // 0
			16, y1, 16, // 1
			 0, y0, 16, // 2
			
			16, y2,  0, // 0
			 0, y0, 16, // 2
			 0, y3,  0, // 3
		};
		
		IBlockData above = world.getBlock((int)x, (int)y + 1, (int)z);
		if(above.getBlockId() != Blocks.WATER.getBlockId()) {
			TextureAtlas atlas = ProjectEdit.getInstance().getTextureManager().getBlockAtlas().getMain();
			int uv_id = atlas.getImageId("block/water_still");
			if(uv_id < 0) uv_id = 0;
			
			AtlasUv uv = atlas.getUv(uv_id);
			
			float uv_x = uv.x1;
			float uv_y = uv.y1;
			float uv_w = uv.x1 + 16 / (float)atlas.getWidth();
			float uv_h = uv.y1 + 16 / (float)atlas.getHeight();
			
			float[] array = new float[] {
				uv_x, uv_h, // 0
				uv_w, uv_h, // 1
				uv_w, uv_y, // 2
				uv_x, uv_h, uv_w, uv_y,
				uv_x, uv_y, // 3
			};
			builder.translucent.uv(array);
			builder.translucent.uv(new float[] {
				uv_w, uv_y, // 1
				uv_w, uv_h, // 2
				uv_x, uv_h, // 3
				
				uv_w, uv_y, // 1
				uv_x, uv_h, // 3
				uv_x, uv_y, // 0
			});
			
			int rgb = BiomeBlend.get(biome.getTemperature(), biome.getDownfall());
			float col_r = ((rgb >> 16) & 0xff) / 255.0f;
			float col_g = ((rgb >>  8) & 0xff) / 255.0f;
			float col_b = ((rgb      ) & 0xff) / 255.0f;
			float[] col = new float[] { col_r * 0.6f, col_g * 0.6f, col_b * 2, 1.0f };
			
			for(int vi = 0, len = top.length; vi < len; vi += 3) {
				float ax = (top[vi] / 16.0f) + xp;
				float ay = (top[vi + 1] / 16.0f) + yp;
				float az = (top[vi + 2] / 16.0f) + zp;
				
				builder.translucent.pos(ax, ay, az);
				builder.translucent.color(col);
			}
		}
	}
	
	private static int getWaterHeight(World world, int x, int y, int z) {
		IBlockData b00 = world.getBlock(x    , y, z    );
		IBlockData b10 = world.getBlock(x + 1, y, z    );
		IBlockData b01 = world.getBlock(x    , y, z + 1);
		IBlockData b11 = world.getBlock(x + 1, y, z + 1);
		
		Object s00 = b00.getStateList().getState(IBlockState.States.av);
		Object s10 = b10.getStateList().getState(IBlockState.States.av);
		Object s01 = b01.getStateList().getState(IBlockState.States.av);
		Object s11 = b11.getStateList().getState(IBlockState.States.av);

		int count = (s00 != null ? 1:0)
				  + (s10 != null ? 1:0)
				  + (s01 != null ? 1:0)
				  + (s11 != null ? 1:0);
		
		if(count == 0) return 0;
		
		return ((s00 != null ? Integer.parseInt(s00.toString()):0)
			 + (s10 != null ? Integer.parseInt(s10.toString()):0)
			 + (s01 != null ? Integer.parseInt(s01.toString()):0)
			 + (s11 != null ? Integer.parseInt(s11.toString()):0)) / count;
	}
	
	private static int getShownFaces(World world, int x, int y, int z) {
		IBlockData origin = world.getBlock(x, y, z);
		BlockShape originShape = FastModelRenderer.getBlockShape(origin);
		
		int flags = 0;
		for(Direction direction : Direction.getFaces()) {
			Vector3f normal = direction.getNormal();
			
			IBlockData adjacent = world.getBlock(x + (int)normal.x, y + (int)normal.y, z + (int)normal.z);
			BlockShape adjacentShape = FastModelRenderer.getBlockShape(adjacent);
			
			boolean isSideBlocked = originShape.isSideBlockedBy(adjacentShape, direction)
				&& !(!FastModelRenderer.hasTranslucency(origin) && FastModelRenderer.hasTranslucency(adjacent));
			
			if(!isSideBlocked) {
				flags |= direction.getFlags();
			}
		}
		
		return flags;
	}
	
	private ChunkList list;
	public void renderWorld(World world, ShaderObjectImpl shader, Camera camera, Matrix4f projectionView, int radius, int flags) {
		if(list == null) {
			list = new ChunkList();
		}
		
		list.render(world, shader, camera, projectionView, radius, flags);
	}
	
	private class ChunkBlob {
		private final Position pos;
		private final World world;
		private final Chunk chunk;
		private final ChunkSectionBlob[] sections;
		private final MeshBuilder builder = new MeshBuilder();
		
		private ChunkBlob(World world, Chunk chunk, int world_x, int world_z) {
			this.world = world;
			this.chunk = chunk;
			this.pos = Position.get(world_x, 0, world_z);
			this.sections = loadSections();
		}
		
		private ChunkSectionBlob[] loadSections() {
			ChunkSectionBlob[] sections = new ChunkSectionBlob[32];
			for(int y = 0; y < 32; y++) {
				IChunkSection section = chunk.getSection(y - 16);
				if(section != null) {
					sections[y] = new ChunkSectionBlob(world, chunk, pos.offset(0, (y - 16) * 16, 0), section);
				}
			}
			
			return sections;
		}
		
		private boolean isDirty = false;
		private boolean unloaded = false;
		
		public void reload() {
			try {
				if(unloaded) return;
				
				// No calls to GL in here
				builder.reset();
				
				for(int y = 0; y < 32; y++) {
					if(unloaded) return;
					ChunkSectionBlob section = sections[y];
					if(section != null && section.doesRender()) {
						section.render(builder);
					}
				}
				
				if(unloaded) return;
				this.isDirty = true;
				new_chunk_loaded = true;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		public void prepare() {
			if(unloaded) return;
			if(chunk.isDirty()) {
				chunk.isDirty = false;
				// When the chunk is reloading we still render it
				reload();
			}
			
			if(isDirty) {
				isDirty = false;
				builder.build();
			}
		}
		
		public void renderOpaque() {
			if(unloaded) return;
			
			Mesh mesh = builder.getMesh(MeshBuilder._OPAQUE);
			if(mesh != null) {
				mesh.render();
			}
		}
		
		public void renderTranslucent() {
			if(unloaded) return;
			
			Mesh mesh = builder.getMesh(MeshBuilder._TRANSLUCENT);
			if(mesh != null) {
				mesh.render();
			}
		}
		
		public void unload() {
			unloaded = true;
			builder.cleanup();
		}
	}

	// 4 bytes = 1 float
	// 8 floats = 1 vert
	// 6 vert = 1 face
	// 6 face = 1 block
	// 5 block = 1 fence
	// 65536 fence = 1 chunk
	
	private static class ChunkSectionBlob {
		private final World world;
		private final Chunk parent;
		private final IChunkSection section;
		private final Position pos;
		
		public ChunkSectionBlob(World world, Chunk parent, Position pos, IChunkSection section) {
			this.world = world;
			this.parent = parent;
			this.section = section;
			this.pos = pos.offset(0, 0, 0);
		}
		
		public void render(MeshBuilder builder) {
			if(!(section instanceof ChunkSection)) return;
			final int bx = pos.getBlockX();
			final int by = pos.getBlockY();
			final int bz = pos.getBlockZ();
			
			ChunkSection sec = (ChunkSection)section;
			for(int i = 0; i < 4096; i++) {
				IBlockData state = sec.blocks[i];
				if(state == null || state.isAir()) continue;
				
				final int wx = bx + (i & 15);
				final int wy = by + (i >> 8);
				final int wz = bz + ((i >> 4) & 15);
				int flags = getShownFaces(world, wx, wy, wz);
				
				// Only perform this check if all blocks in a 5x5x5 area are blocking.
				// TODO: Update geometry when this happenenes
//				if(flags != 0) {
					renderBlockWithBiome(world, state, parent.getBiome(wx, wy, wz), pos, wx & 15, wy, wz & 15, builder, flags);
//				}
			}
		}
		
		public boolean doesRender() {
			return section.isLoaded();
		}
	}
	
	private static final long timeout = 1000;
	private static final long load_limit = 100;
	private static ThreadPoolExecutor exec = (ThreadPoolExecutor)Executors.newFixedThreadPool(7);
	
	protected boolean new_chunk_loaded;
	public void cleanup() {
		exec.shutdown();
	}
	
	public void unloadCache() {
		if(list != null) {
			list.unloadCache();
		}
	}
	
	public static final int FRUSTUM_CULLING = 1;
	public static final int DRAW_TRANSLUCENT = 2;
	
	private class ChunkList {
		private final Map<Long, ChunkBlob> chunk_map;
		private final Map<ChunkBlob, Long> time_map;
		
		public ChunkList() {
			chunk_map = new HashMap<>();
			time_map = new HashMap<>();
		}
		
		public void unloadCache() {
			for(ChunkBlob blob : time_map.keySet()) {
				blob.unload();
			}
			
			time_map.clear();
			chunk_map.clear();
		}
		
		// TODO: Chunks coordinates should be local to the camera. +- 512 blocks. Never larger or smaller than that.
		// Otherwise we get weird floating point jumps
		public void render(World world, ShaderObjectImpl shader, Camera camera, Matrix4f projectionView, int radius, int flags) {
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
					int bx = blob.chunk.getX();
					int bz = blob.chunk.getZ();
					
					if(bx < uxs || bx > uxe || bz < uzs || bz > uze) {
						if(now > time_map.get(blob)) {
							long idx = blob.chunk.getPair();
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
			
			boolean frustumCulling = (flags & FRUSTUM_CULLING) != 0;
			long loaded = load_limit;
			
			Vector3f aa = new Vector3f();
			Vector3f bb = new Vector3f();
			
			FrustumIntersection intersect = new FrustumIntersection(projectionView);
			for(int i = xs + 1; i < xe; i++) {
				for(int j = zs + 1; j < ze; j++) {
					if(frustumCulling && !intersect.testAab(aa.set(i * 16, -256, j * 16), bb.set(i * 16 + 16, 256, j * 16 + 16))) {
						continue;
					}
					
					long idx = ((long)(i) & 0xffffffffL) | (((long)j) << 32L);
					ChunkBlob blob = chunk_map.get(idx);
					if(blob == null) {
						if(exec.getQueue().size() > 300) {
							continue;
						}
						
						IChunk chunk = world.getChunk(i, j);
						if(chunk == null || !chunk.isLoaded()) continue;
						
						if(loaded-- > 0) {
							blob = new ChunkBlob(world, (Chunk)chunk, i * 16, j * 16);
							final ChunkBlob testblob = blob;
							time_map.put(blob, now + timeout);
							chunk_map.put(idx, blob);
							exec.submit(testblob::reload);
						} else {
							continue;
						}
					}
					
					if(blob != null) {
						blob.prepare();
					}
				}
			}
			
			if(shader instanceof MeshShader) {
				shader.setProjectionMatrix(camera.getProjectionMatrixTest());
			}
			
			for(int i = xs + 1; i < xe; i++) {
				for(int j = zs + 1; j < ze; j++) {
					if(frustumCulling && !intersect.testAab(aa.set(i * 16, -256, j * 16), bb.set(i * 16 + 16, 256, j * 16 + 16))) {
						continue;
					}
					
					long idx = ((long)(i) & 0xffffffffL) | (((long)j) << 32L);
					ChunkBlob blob = chunk_map.get(idx);
					if(blob != null) {
						shader.setTranslationMatrix(getChunkMatrix(i - x, j - z));
						blob.renderOpaque();
					}
				}
			}
			
			if((flags & DRAW_TRANSLUCENT) != 0) {
				GL11.glEnable(GL11.GL_BLEND);
				for(int i = xs + 1; i < xe; i++) {
					for(int j = zs + 1; j < ze; j++) {
						if(frustumCulling && !intersect.testAab(aa.set(i * 16, -256, j * 16), bb.set(i * 16 + 16, 256, j * 16 + 16))) {
							continue;
						}
						
						long idx = ((long)(i) & 0xffffffffL) | (((long)j) << 32L);
						ChunkBlob blob = chunk_map.get(idx);
						if(blob != null) {
							shader.setTranslationMatrix(getChunkMatrix(i - x, j - z));
							blob.renderTranslucent();
						}
					}
				}
				GL11.glDisable(GL11.GL_BLEND);
				RenderUtil.disableDepthTest();
				RenderUtil.enableDepthTest();
				RenderUtil.setDepthFunc(DepthFunc.LEQUAL);
			}
		}
	}
	
	private static Matrix4f chunkMatrix = new Matrix4f();
	private static Matrix4f getChunkMatrix(int x, int z) {
		return chunkMatrix.identity().translate(x * 16, 0, z * 16);
	}
}
