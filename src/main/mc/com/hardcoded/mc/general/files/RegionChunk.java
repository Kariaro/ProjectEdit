package com.hardcoded.mc.general.files;

import com.hardcoded.mc.general.ByteBuf;
import com.hardcoded.mc.general.nbt.*;

public class RegionChunk {
	private NBTTagCompound nbt;
	public SubChunk[] chunks = new SubChunk[16];
	
	public RegionChunk(ByteBuf buf) {
		this.nbt = NBTBase.readNBTTagCompound(buf);
		read();
	}
	
	@SuppressWarnings("unchecked")
	public void read() {
		NBTTagCompound level = (NBTTagCompound)nbt.get("Level");
		NBTTagList<NBTTagCompound> sections = (NBTTagList<NBTTagCompound>)level.get("Sections");
		
		NBTTagCompound[] nbt_sections = new NBTTagCompound[16];
		//System.out.println("y: vALUES, " + sections.size());
		for(NBTTagCompound entry : sections) {
			int y = ((NBTTagByte)entry.get("Y")).getValue();
			
			if(y < 0 || y > 15) continue;
			nbt_sections[y] = entry;
			chunks[y] = new SubChunk(entry, y);
		}
	}
	
	public SubChunk getSubChunk(int y) {
		return chunks[y];
	}
	
	public class SubChunk {
		public NBTTagCompound nbt;
		public int[] data = new int[4096];
		public int y;
		
		public SubChunk(NBTTagCompound nbt, int y) {
			this.nbt = nbt;
			load();
		}
		
		@SuppressWarnings("unchecked")
		public void load() {
			NBTTagList<NBTTagCompound> palette = (NBTTagList<NBTTagCompound>)nbt.get("Palette");
			if(palette == null) return;
			
			int bits_per_block = Integer.bitCount(Integer.highestOneBit(palette.size()) - 1) + 1;
			if(bits_per_block < 4) {
				bits_per_block = 4;
			} else if(bits_per_block < 9) {
				
			} else {
				bits_per_block = 14;
			}
			
			NBTTagLongArray blockStates = (NBTTagLongArray)nbt.get("BlockStates");
			long[] array = blockStates.getArray();
			String[] palette_test = new String[palette.size()];
			for(int i = 0, len = palette_test.length; i < len; i++) {
				NBTTagCompound entry = palette.get(i);
				String name = ((NBTTagString)entry.get("Name")).getValue();
				palette_test[i] = name;
			}
			
			data = getBlockArrayTest(array, palette_test, bits_per_block, data.length);
		}
		
		private int[] getBlockArrayTest(long[] data, String[] palette, int bits, int amount) {
			int[] ret = new int[amount];
			
			long mask = (1 << (bits + 0L)) - 1;
			long offset = 0;
			int position = 0;
			
			for(int i = 0; i < data.length; i++) {
				long value = data[i] >>> offset;
				
				for(int j = 0; j <= 63 / bits; j++) {
					long bitPos = j * bits;
					
					long id = value & mask;
					value >>>= bits;
					
					if(bitPos + offset + bits > 64) {
						long pos = ((bitPos + bits + offset) & 63);
						long next = 0;
						if(i + 1 < data.length) {
							next = (data[i + 1] & ((1 << pos) - 1)) << (bits - pos);
						}
						
						id |= next;
						if(position < amount) {
							if(id < palette.length) {
								String name = palette[(int)id];
								ret[position++] = name.hashCode();
							}
						}
						
						offset = pos;
						break;
					}
					
					if(position < amount) {
						if(id < palette.length) {
							String name = palette[(int)id];
							ret[position++] = name.hashCode();
						}
					}
				}
			}
			
			return ret;
		}
		
		
		private int[] getBlockArrayFast(long[] data, int[] palette, int bits, int amount) {
			int[] ret = new int[amount];
			
			long mask = (1 << (bits + 0L)) - 1;
			long offset = 0;
			int position = 0;
			
			for(int i = 0; i < data.length; i++) {
				long value = data[i] >>> offset;
				
				for(int j = 0; j <= 63 / bits; j++) {
					long bitPos = j * bits;
					
					long id = value & mask;
					value >>>= bits;
					
					if(bitPos + offset + bits > 64) {
						long pos = ((bitPos + bits + offset) & 63);
						long next = 0;
						if(i + 1 < data.length) {
							next = (data[i + 1] & ((1 << pos) - 1)) << (bits - pos);
						}
						
						id |= next;
						if(position < amount) {
							ret[position++] = palette[(int)id];
						}
						
						offset = pos;
						break;
					}
					
					if(position < amount) {
						ret[position++] = palette[(int)id];
					}
				}
			}
			
			return ret;
		}
	}
	
	public NBTTagCompound getNBT() {
		return nbt;
	}
}
