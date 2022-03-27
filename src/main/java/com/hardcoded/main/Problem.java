package com.hardcoded.main;

public class Problem {
	public static void main(String[] args) {
		int result;
//		result = recurseStates(40, 54);
//		result = recurseStatesBits(1024, 54);
		result = recurseStatesNew(1024, 54);
		
		System.out.printf("Result: %d\n", result);
		
//		recurseStatesBits(15, 1);
	}
	
	public static int recurseStates(int items, int maxPairs) {
		int[] numbers = new int[items];
		for(int i = 0; i < numbers.length; i++) {
			numbers[i] = i;
		}
		
		return recurseStates(0, maxPairs, numbers);
	}
	
	// Convert all numbers 90 degrees where the bits represent each row
	
	private static class Bits {
		private final long[] data;
		private int count;
		
		public Bits(int length) {
			data = new long[(length + 63) >> 6];
		}
		
		public void set(int idx, boolean enable) {
			final long bits = idx & 63L;
			long read = data[idx >> 6];
			boolean set = get(idx);
			
			if(enable) {
				data[idx >> 6] = read | (1L << bits);
				count += set != enable ? 1:0;
			} else {
				data[idx >> 6] = read & ~(1L << bits);
				count -= set != enable ? 1:0;
			}
		}
		
		public boolean get(int idx) {
			return (data[idx >> 6] & (1L << (idx & 63L))) != 0;
		}
		
		public int findBit() {
			return findBit(0);
		}
		
		public int findBit(int startIndex) {
			for(int i = (startIndex >> 6), len = data.length; i < len; i++) {
				long read = data[i];
				
				if(read != 0) {
					return (i << 6) + Long.numberOfTrailingZeros(Long.lowestOneBit(read));
				}
			}
			
			return -1;
		}
		
		public int count() {
			return count;
		}
	}
	
	public static int lowestDepth;
	public static int recurseStatesNew(int numberOfItems, int itemsPerContainer) {
		int[] numbers = new int[numberOfItems];
		for(int i = 0; i < numberOfItems; i++) {
			numbers[i] = i;
		}
		
		lowestDepth = Integer.MAX_VALUE;
		return recurseStatesNew(0, numberOfItems, numbers, itemsPerContainer);
	}
	
	public static int recurseStatesNew(int depth, int numberOfItems, int[] numbers, int itemsPerContainer) {
		if(depth > lowestDepth) {
			return lowestDepth;
		}
		
		int bit = getBestPattern(numbers, itemsPerContainer);
		
		int[] choices = new int[numberOfItems];
		int minCount = Integer.MAX_VALUE;
		int count = 0;
		for(int i = 0; i < numberOfItems; i++) {
			if((numbers[i] & bit) != 0) {
				choices[count++] = i;
			}
		}
		
//		System.out.printf("Count: %d\n", count);
		if(count == 0) {
			if(lowestDepth > depth) {
				lowestDepth = depth;
				System.out.printf("Best pattern: %d, %d, %s\n", lowestDepth, depth, toBinary(bit, 10));
			}
//			for(int i = 0; i < numberOfItems; i++) {
//				System.out.printf("%4d, %s\n", i, toBinary(numbers[i], 10));
//			}
//			System.out.println();
			return depth;
		}
		
		if(count < itemsPerContainer) {
			for(int j = 0; j < count; j++) numbers[choices[j]] &= ~bit;
			minCount = Math.min(minCount, recurseStatesNew(depth + 1, numberOfItems, numbers, itemsPerContainer));
			for(int j = 0; j < count; j++) numbers[choices[j]] |= bit;
		} else {
			int count_len = count - itemsPerContainer;
			
			for(int j = 0; j < itemsPerContainer; j++) numbers[choices[j]] &= ~bit;
			
			// Branch here
			for(int j = 0; j < count_len; j++) {
				numbers[choices[j + itemsPerContainer]] &= ~bit;
				numbers[choices[j]] |= bit;
				minCount = Math.min(minCount, recurseStatesNew(depth + 1, numberOfItems, numbers, itemsPerContainer));
			}
			for(int j = count - itemsPerContainer; j < count; j++) numbers[j] |= bit;
		}
		
		return minCount;
	}
	
	// Return lists.
	private static int getBestPattern(int[] numbers, int itemsPerContainer) {
		final int numberOfItems = numbers.length;
		final int items = Integer.highestOneBit(numberOfItems) << 1;
		
		int bestPatternCount = 0;
		int bestPattern = 0;
		int bestTotal = 0;
		for(int pattern = 1; pattern < items; pattern++) {
			int count = 0;
			for(int i = 0; i < numberOfItems; i++) {
				int value = numbers[i] & pattern;
				count += (value == pattern ? 1:0);
			}
			
			// TODO: Return all combinations of this pattern.
			int patternCount = Integer.bitCount(pattern);
			int capCount = count > itemsPerContainer ? itemsPerContainer:count;
			int capTotal = capCount * patternCount;
			
			if(bestTotal < capTotal || (bestTotal == capTotal && patternCount > bestPatternCount)) {
				bestTotal = capTotal;
				bestPatternCount = patternCount;
				bestPattern = pattern;
			}
		}
		
		return bestPattern;
	}
	
	
	public static int recurseStatesBits(int numberOfItems, int itemsPerContainer) {
		final int bit_length = 32 - Integer.numberOfLeadingZeros(numberOfItems);
		Bits[] bits = new Bits[bit_length];
		for(int i = 0; i < bit_length; i++) {
			bits[i] = new Bits(numberOfItems);
		}
		
		// TODO: Figure out the best bit configuration for the items
		// Generate all items from [0 .. numberOfItems)
		// And place their bits into the bit array.
		for(int i = 0; i < numberOfItems; i++) {
			int value = i;
			
			while(value != 0) {
				long bit = Long.lowestOneBit(value);
				value &= ~bit;
				bits[Long.numberOfTrailingZeros(bit)].set(i, true);
			}
		}
		
		// This is the conversion we are doing
		// Flipping from row to column
		//	  5 4 3 2 1
		// 1: 1 0 1 0 1
		// 2: 1 0 0 0 1
		// 3: 1 1 1 0 0
		// 4: 1 1 1 1 0
		
		int count = 0;
		// Find the highest bit count
		while(true) {
			int highest = 0;
			Bits bit = null;
			
			for(int i = 0; i < bit_length; i++) {
				Bits b = bits[i];
				if(b.count > highest) {
					highest = b.count;
					bit = b;
				}
			}
			
			if(bit == null) {
				return count;
			}
			
			// Create chest of size 'itemsPerContainer'
			for(int i = 0, j = 0; i < itemsPerContainer; i++) {
				j = bit.findBit(j);
				if(j < 0) break;
				bit.set(j, false);
			}
			
			count ++;
		}
	}
	
	public static String toBinary(long value, int zeros) {
		return ("%" + zeros + "s").formatted(Long.toBinaryString(value)).replace(' ', '0');
	}
	
	public static int recurseStates(int depth, int itemsPerContainer, int[] numbers) {
		final int number_len = numbers.length;
		
		int[] choices = new int[number_len];
		int maxCount = Integer.MAX_VALUE;
		
		boolean isEmpty = true;
		for(int i = 0; i < number_len; i++) {
			int value = numbers[i];
			
			// Find a matching pair with the same highest bit
			if(value != 0) {
				// If not all values was zero we are not empty yet.
				isEmpty = false;
				
				// Pick a bit and remove it from our value
				int bit = Integer.lowestOneBit(value);
				
				int count = 0;
				for(int j = 0; j < number_len; j++) {
					if((numbers[j] & bit) != 0) {
						choices[count++] = j;
					}
				}
				
				if(count < itemsPerContainer) {
					for(int j = 0; j < count; j++) numbers[choices[j]] &= ~bit;
					maxCount = Math.min(maxCount, recurseStates(depth + 1, itemsPerContainer, numbers));
					for(int j = 0; j < count; j++) numbers[choices[j]] |= bit;
				} else {
					int count_len = count - itemsPerContainer;
					
					for(int j = 0; j < itemsPerContainer; j++) numbers[choices[j]] &= ~bit;
					
					// Branch here
					for(int j = 0; j < count_len; j++) {
						numbers[choices[j + itemsPerContainer]] &= ~bit;
						numbers[choices[j]] |= bit;
						maxCount = Math.min(maxCount, recurseStates(depth + 1, itemsPerContainer, numbers));
					}
					
					for(int j = count - itemsPerContainer; j < count; j++) numbers[j] |= bit;
				}
				
				if(depth > maxCount) {
					return maxCount;
				}
			}
		}
		
		return isEmpty ? depth:maxCount;
	}
}