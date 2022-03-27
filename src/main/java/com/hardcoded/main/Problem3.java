package com.hardcoded.main;

import java.util.Arrays;

public class Problem3 {
	public static void main(String[] args) {
		int result;
		result = recurseStatesNew(6, 2);
		
		System.out.printf("Result: %d\n", result);
	}
	
	public static int lowestDepth;
	public static int recurseStatesNew(int numberOfItems, int itemsPerContainer) {
		int[] numbers = new int[numberOfItems];
		for(int i = 0; i < numberOfItems; i++) {
			numbers[i] = i;
		}
		
		StackObject stack = new StackObject();
		
		lowestDepth = Integer.MAX_VALUE;
		return recursePattern(0, numberOfItems, numbers, stack, itemsPerContainer);
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
	
	public static int recursePattern(int depth, int numberOfItems, int[] numbers, StackObject stack, int itemsPerContainer) {
		if(depth > lowestDepth) {
			return lowestDepth;
		}
		
		// TODO: Test for all combinations of this pattern. (Best first)
		final int nextDepth = depth + 1;
		final int pattern = getBestPattern(numbers, itemsPerContainer);
		final int invPattern = ~pattern;
		
		int[] choices = new int[numberOfItems];
		int minCount = Integer.MAX_VALUE;
		int count = 0;
		for(int i = 0; i < numberOfItems; i++) {
			if((numbers[i] & pattern) == pattern) {
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
					int topPattern = top.pattern;
					StringBuilder sb = new StringBuilder();
					sb.append("---------- ").append(topDepth--).append("\n")
					  .append("Pattern: %s\n".formatted(toBinary(topPattern, 10)))
					  .append("Items: %d\n".formatted(top.choices.length))
					  .append("Index: [");
					for(int i : top.choices) {
						if((i & topPattern) != topPattern) sb.append("(error)");
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
		
		if(count < itemsPerContainer) {
			for(int j = 0; j < count; j++) numbers[choices[j]] &= invPattern;
			minCount = Math.min(minCount, recursePattern(nextDepth, numberOfItems, numbers, stack.child(pattern, Arrays.copyOf(choices, count)), itemsPerContainer));
			for(int j = 0; j < count; j++) numbers[choices[j]] |= pattern;
		} else {
			final int count_len = count - itemsPerContainer;
			
			for(int j = 0; j < itemsPerContainer; j++) numbers[choices[j]] &= invPattern;
			
			minCount = recursePattern(nextDepth, numberOfItems, numbers, stack.child(pattern, Arrays.copyOfRange(choices, 0, itemsPerContainer)), itemsPerContainer);
			
			// Branch here
			for(int j = 0, k = j + itemsPerContainer; j < count_len; j++, k++) {
				numbers[choices[k]] &= invPattern;
				numbers[choices[j]] |= pattern;
				if(minCount >= depth) {
					minCount = Math.min(minCount, recursePattern(nextDepth, numberOfItems, numbers, stack.child(pattern, Arrays.copyOfRange(choices, j, k)), itemsPerContainer));
				}
			}
			
			for(int j = count_len; j < count; j++) numbers[choices[j]] |= pattern;
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