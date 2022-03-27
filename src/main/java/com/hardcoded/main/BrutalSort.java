package com.hardcoded.main;

import java.util.*;

public class BrutalSort {
	public static void main(String[] args) {
		test();
	}
	
	public static void test() {
		int[] values = { 5, 3, 2, 4 };
		int[] sorted = sortArray(values);
		
		System.out.println("SortedArray: " + Arrays.toString(sorted));
	}
	
	public static int[] sortArray(int[] array) {
		final int length = array.length;
		List<List<Node>> list = createPermutations(array);

		// For all of the first input
		for(int i = 0; i < length; i++) {
			final int listLength = list.size();
			if(listLength == 1) {
				break;
			}
			
			List<Node> indexList = new ArrayList<>();
			for(int j = 0; j < listLength; j++) {
				indexList.add(new Node(j, list.get(j).get(i).value));
			}
			
			List<List<Node>> ascendingLists = new ArrayList<>();
			
			// Create permutations of first index
			List<List<Node>> indexPermutations = createPermutations(indexList.toArray(Node[]::new));
			int indexPermutationsLen = indexPermutations.size();
			
			for(int j = 0; j < indexPermutationsLen; j++) {
				boolean matchesPattern = true;
				List<Node> permutation = indexPermutations.get(j);
				final int permutationLen = permutation.size();
				int l = Integer.MIN_VALUE;
				for(int k = 0; k < permutationLen; k++) {
					Node value = permutation.get(k);
					if(value.value < l) {
						matchesPattern = false;
						break;
					}
					
					l = value.value;
				}
				
				if(matchesPattern) {
					ascendingLists.add(permutation);
				}
			}
			
			Integer last = null;
			
			List<List<Node>> nextPermutations = new ArrayList<>();
			Set<Integer> uniquePermutations = new HashSet<>();
			
			// Cut the lists where the numbers have changed.
			for(int j = 0, len = ascendingLists.size(); j < len; j++) {
				List<Node> listEntry = ascendingLists.get(j);
				Node value = listEntry.get(i);
				if(last == null || last == value.value) {
					last = value.value;
					if(uniquePermutations.add(value.index)) {
						nextPermutations.add(list.get(value.index));
					}
				} else if(last != value.value) {
					// Cut list here
					break;
				}
			}
			
			// Set next list permutations to current permutations
			list = nextPermutations;
		}
		
		return list.get(0).stream().mapToInt(i -> i.value).toArray();
	}
	
	public static List<Node> createNodes(int... array) {
		List<Node> list = new ArrayList<>(array.length);
		for(int i = 0, len = array.length; i < len; i++) {
			list.add(new Node(i, array[i]));
		}
		
		return list;
	}
	
	public static List<List<Node>> createPermutations(int... array) {
		List<List<Node>> list = new ArrayList<>();
		_recurse(list, new LinkedHashSet<>(), array);
		return list;
	}
	
	public static List<List<Node>> createPermutations(Node... array) {
		List<List<Node>> list = new ArrayList<>();
		_recurse(list, new LinkedHashSet<>(), array);
		return list;
	}
	
	public static void _recurse(List<List<Node>> list, Set<Integer> bag, Node[] array) {
		final int length = array.length;
		
		if(bag.size() == length) {
			List<Node> nodeList = new ArrayList<>(length);
			
			for(Integer index : bag) {
				nodeList.add(new Node(index, array[index].value));
			}
			
			list.add(nodeList);
			
			return;
		}
		
		for(int i = 0; i < length; i++) {
			if(!bag.contains(i)) {
				bag.add(i);
				_recurse(list, bag, array);
				bag.remove(i);
			}
		}
	}
	
	public static void _recurse(List<List<Node>> list, Set<Integer> bag, int[] array) {
		final int length = array.length;
		
		if(bag.size() == length) {
			List<Node> nodeList = new ArrayList<>(length);
			
			for(Integer index : bag) {
				nodeList.add(new Node(index, array[index]));
			}
			
			list.add(nodeList);
			
			return;
		}
		
		for(int i = 0; i < length; i++) {
			if(!bag.contains(i)) {
				bag.add(i);
				_recurse(list, bag, array);
				bag.remove(i);
			}
		}
	}
	
	static class Node {
		public final int index;
		public final int value;
		
		public Node(int index, int value) {
			this.index = index;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}
}
