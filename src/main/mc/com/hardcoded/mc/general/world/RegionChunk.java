package com.hardcoded.mc.general.world;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.nbt.*;

public class RegionChunk {
	private NBTTagCompound nbt;
	public SubChunk[] sections = new SubChunk[16];
	
	public RegionChunk(ByteBuf buf) {
		this.nbt = NBTBase.readNBTTagCompound(buf);
		read();
	}
	
	@SuppressWarnings("unchecked")
	public void read() {
		NBTTagCompound level = (NBTTagCompound)nbt.get("Level");
		NBTTagList<NBTTagCompound> sections = (NBTTagList<NBTTagCompound>)level.get("Sections");
		
		NBTTagCompound[] nbt_sections = new NBTTagCompound[16];
		for(NBTTagCompound entry : sections) {
			int y = ((NBTTagByte)entry.get("Y")).getValue();
			
			if(y < 0 || y > 15) continue;
			nbt_sections[y] = entry;
			this.sections[y] = new SubChunk(entry, y);
		}
	}
	
	public SubChunk getSubChunk(int y) {
		return sections[y];
	}
	
	private static int ceillog2(int i) {
		return i < 1 ? 0:(32 - Integer.numberOfLeadingZeros(i - 1));
	}
	public class SubChunk {
		public IBlockState[] blocks = new IBlockState[4096];
		public NBTTagCompound nbt;
		public int y;
		
		public SubChunk(NBTTagCompound nbt, int y) {
			this.nbt = nbt;
			this.y = y;
			load();
		}
		
		@SuppressWarnings("unchecked")
		public void load() {
			NBTTagList<NBTTagCompound> palette = (NBTTagList<NBTTagCompound>)nbt.get("Palette");
			if(palette == null) return;
			
			NBTTagLongArray blockStates = (NBTTagLongArray)nbt.get("BlockStates");
			int bits_per_block = Math.max(4, ceillog2(palette.size()));
			//bits_per_block = Integer.bitCount(Integer.highestOneBit(palette.size()) - 1) + 1;
			
			long[] array = blockStates.getArray();
			String[] palette_test = new String[palette.size()];
			for(int i = 0, len = palette_test.length; i < len; i++) {
				NBTTagCompound entry = palette.get(i);
				String name = ((NBTTagString)entry.get("Name")).getValue();
				palette_test[i] = name;
			}
			
			compute_blocks(array, palette_test, bits_per_block);
		}
		
		private void compute_blocks(long[] data, String[] palette, long bits) {
			final long mask = (1L << bits) - 1;
			final long gt = 64 - bits;
			for(int i = 0, index = 0; i < 4096; index += bits, i++) {
				final long offset = (index & 63);
				int value = (int)((data[index >> 6] >>> offset) & mask);
				blocks[i] = BlockStates.getState(palette[value]);
				final long next_offset = (index + bits) & 63;
				index += (next_offset > gt) ? (64 - next_offset):0;
			}
		}
	}
	
	public NBTTagCompound getNBT() {
		return nbt;
	}
}
