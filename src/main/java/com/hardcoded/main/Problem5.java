package com.hardcoded.main;

import java.util.*;

public class Problem5 {
	public static final int MAX_ITEMS = Integer.MAX_VALUE;
	
	public static void main(String[] args) {
		System.out.printf("Result: %d\n", recurseStatesNew(1024, 54));
		
//		for(int i = 16; i < 1000; i += 16) {
//			int times = recurseStatesNew(i, 54);
//			double res = i / (double)times;
//			System.out.printf("Ratio: index: %d, result: %d, ratio: %.4f\n", i, times, res);
//		}
	}
	
	public static int lowestDepth;
	public static int recurseStatesNew(int numberOfItems, int itemsPerContainer) {
		int[] numbers = new int[numberOfItems];
		for(int i = 0; i < numberOfItems; i++) {
			numbers[i] = i;
		}
		
		StackObject stack = new StackObject();
		
		lowestDepth = Integer.MAX_VALUE;
		return recursePattern(0, numbers, stack, itemsPerContainer);
	}
	
	public static class StackObject {
		public StackObject parent;
		public final int[] choices;
		public final int pattern;
		
		public StackObject() {
			this(null, 0, null);
		}
		
		public StackObject(StackObject parent, int pattern, int[] choices) {
			this.parent = parent;
			this.pattern = pattern;
			this.choices = choices;
		}
		
		public StackObject child(int pattern, int[] choices) {
			return new StackObject(this, pattern,  choices);
		}
	}
	
	public static int recursePattern(int depth, int[] numbers, StackObject stack, int itemsPerContainer) {
		if(depth > lowestDepth) {
			return lowestDepth;
		}
		
		// Test for all combinations of this pattern. (Best first)
		int[] patternList = getBestPatternList(numbers, itemsPerContainer);
		
		if(patternList.length == 0 && lowestDepth > depth) {
			lowestDepth = depth;
			
			// System.out.printf("Best pattern: %d, %d, %s\n", lowestDepth, depth, toBinary(bit, 10));
			
			StackObject top = stack;
			int topDepth = depth;

			StringBuilder sb = new StringBuilder();
			
			int[] average = new int[numbers.length];
			
			while(top != null && top.pattern != 0) {
				int topPattern = top.pattern;
				sb.append("---------- ").append(topDepth--).append("\n")
				  .append("Pattern: %s\n".formatted(toBinary(topPattern, Integer.numberOfTrailingZeros(Integer.highestOneBit(numbers.length)) + 1)))
				  .append("Items: %d\n".formatted(top.choices.length))
				  .append("Index: [");
				for(int i : top.choices) {
					sb.append(((i & topPattern) != topPattern) ? "(error)":"").append(i).append(", ");
					average[i]++;
				}
				sb.delete(sb.length() - 2, sb.length());
				sb.append("]\n\n");
				
				top = top.parent;
			}
			
			System.out.println(sb.toString());
			System.out.println(Arrays.toString(average));
			System.out.println("\n\n");
			
			return lowestDepth;
		}
		
		final int number_len = numbers.length;
		final int[] choices = new int[number_len];
		final int nextDepth = depth + 1;
		int minCount = lowestDepth;
		
		for(int value : patternList) {
			final int pattern = value & 0xffff;
			final int invPattern = ~pattern;
			
			int count = 0;
			for(int i = 0; i < number_len; i++) {
				if((numbers[i] & pattern) == pattern) {
					choices[count++] = i;
				}
			}
			
			if(count < itemsPerContainer) {
				for(int j = 0; j < count; j++) numbers[choices[j]] &= invPattern;
				minCount = Math.min(minCount, recursePattern(nextDepth, numbers, stack.child(pattern, Arrays.copyOf(choices, count)), itemsPerContainer));
				for(int j = 0; j < count; j++) numbers[choices[j]] |= pattern;
			} else {
				final int count_len = count - itemsPerContainer;
				
				for(int j = 0; j < itemsPerContainer; j++) numbers[choices[j]] &= invPattern;
				minCount = recursePattern(nextDepth, numbers, stack.child(pattern, Arrays.copyOf(choices, itemsPerContainer)), itemsPerContainer);
				for(int j = 0, k = j + itemsPerContainer; j < count_len; j++, k++) {
					numbers[choices[k]] &= invPattern;
					numbers[choices[j]] |= pattern;
					
					if(minCount > depth) {
						minCount = Math.min(minCount, recursePattern(nextDepth, numbers, stack.child(pattern, Arrays.copyOfRange(choices, j, k)), itemsPerContainer));
					}
				}
				
				for(int j = count_len; j < count; j++) numbers[choices[j]] |= pattern;
			}
		}
		
		return minCount;
	}
	
	private static int[] getBestPatternList(int[] numbers, int itemsPerContainer) {
		final int numberOfItems = numbers.length;
		final int items = Integer.highestOneBit(numberOfItems) << 1;
		
		int index = 0;
		int[] patterns = new int[numberOfItems - 1];
		for(int pattern = 1; pattern < items; pattern++) {
			int count = 0;
			for(int i = 0; i < numberOfItems; i++) {
				int value = numbers[i] & pattern;
				count += (value == pattern ? 1:0);
			}
			
			if(count > 0) {
				int valueCount = (count > itemsPerContainer ? itemsPerContainer:count) * Integer.bitCount(pattern);
				patterns[index++] = (valueCount << 16) | pattern;
			}
		}
		
		// If value count is equal it's gonna check for the pattern size.
		// The pattern is encoded in the lower 16 bits.
		Arrays.parallelSort(patterns, 0, index);
		
		final int resultLength = Math.min(MAX_ITEMS, index);
		final int[] result = new int[resultLength];
		
		// Swap the items to get the decending order.
		for(int i = 0; i < resultLength; i++) {
			result[i] = patterns[index - i - 1];
		}
		
		return result;
	}
	
	public static String toBinary(long value, int zeros) {
		return ("%" + zeros + "s").formatted(Long.toBinaryString(value)).replace(' ', '0');
	}
}