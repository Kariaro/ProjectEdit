package com.hardcoded.main;

import java.util.Arrays;

public class Problem2 {
	public static void main(String[] args) {
		int result;
		result = recurseStatesNew(1024, 54);
		
		System.out.printf("Result: %d\n", result);
		
//		recurseStatesBits(15, 1);
	}
	
	public static int lowestDepth;
	public static int recurseStatesNew(int numberOfItems, int itemsPerContainer) {
		int[] numbers = new int[numberOfItems];
		for(int i = 0; i < numberOfItems; i++) {
			numbers[i] = i;
		}
		
		StackObject stack = new StackObject();
		
		lowestDepth = Integer.MAX_VALUE;
		return recurseStatesNew(0, numberOfItems, numbers, stack, itemsPerContainer);
	}
	
	public static class StackObject {
		public StackObject parent;
		public final int[] numbers;
		public final int[] choices;
		public final int pattern;
		
		public StackObject() {
			this(null, 0, null, null);
		}
		
		public StackObject(StackObject parent, int pattern, int[] numbers, int[] choices) {
			this.parent = parent;
			this.pattern = pattern;
			this.numbers = numbers;
			this.choices = choices;
		}
		
		public StackObject child(int[] numbers, int pattern, int[] choices) {
			return new StackObject(this, pattern, numbers.clone(), choices);
		}
	}
	
	public static int recurseStatesNew(int depth, int numberOfItems, int[] numbers, StackObject stack, int itemsPerContainer) {
		if(depth > lowestDepth) {
			return lowestDepth;
		}

		final int nextDepth = depth + 1;
		int bit = getBestPattern(numbers, itemsPerContainer);
		
		int[] choices = new int[numberOfItems];
		int minCount = Integer.MAX_VALUE;
		int count = 0;
		for(int i = 0; i < numberOfItems; i++) {
			if((numbers[i] & bit) == bit) {
				choices[count++] = i;
			}
		}
		
		if(count == 0) {
			if(lowestDepth > depth) {
//				System.out.printf("Best pattern: %d, %d, %s\n", lowestDepth, depth, toBinary(bit, 10));
				lowestDepth = depth;
				
				System.out.println("\n\n\n\n\n");
				
				StackObject top = stack;
				int topDepth = depth;
				while(top != null && top.pattern != 0) {
					int pattern = top.pattern;
					StringBuilder sb = new StringBuilder();
					sb.append("---------- ").append(topDepth--).append("\n")
					  .append("Pattern: %s\n".formatted(toBinary(pattern, 10)))
					  .append("Items: %d\n".formatted(top.choices.length))
					  .append("Index: [");
					for(int i : top.choices) {
//						if((i & pattern) != pattern) System.out.println("ERROR: NOT PATTERN");
						sb.append(i).append(", ");
					}
					sb.delete(sb.length() - 2, sb.length());
					sb.append("]\n");
					
					System.out.println(sb.toString());
					top = top.parent;
				}
			}
			
			return depth;
		}
		
		if(count <= itemsPerContainer) {
			for(int j = 0; j < count; j++) numbers[choices[j]] &= ~bit;
			minCount = Math.min(minCount, recurseStatesNew(nextDepth, numberOfItems, numbers, stack.child(numbers, bit, Arrays.copyOf(choices, count)), itemsPerContainer));
			for(int j = 0; j < count; j++) numbers[choices[j]] |= bit;
		} else {
			int count_len = count - itemsPerContainer;
			
			for(int j = 0; j < itemsPerContainer; j++) numbers[choices[j]] &= ~bit;
			
			// Branch here
			for(int j = 0; j < count_len; j++) {
				numbers[choices[j + itemsPerContainer]] &= ~bit;
				numbers[choices[j]] |= bit;
				if(minCount > nextDepth) {
					minCount = Math.min(minCount, recurseStatesNew(nextDepth, numberOfItems, numbers, stack.child(numbers, bit, Arrays.copyOfRange(choices, j, j + itemsPerContainer)), itemsPerContainer));
				}
			}
			
			for(int j = count_len; j < count; j++) numbers[choices[j]] |= bit;
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
	
	public static String toBinary(long value, int zeros) {
		return ("%" + zeros + "s").formatted(Long.toBinaryString(value)).replace(' ', '0');
	}
}