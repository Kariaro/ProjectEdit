package com.hardcoded.main;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		/*
		final int ITERATIONS = 1000;
		final float[] RANDOM_DATA = new float[ITERATIONS * 6];
		final Random RANDOM = new Random(0);
		final int LENGTH = RANDOM_DATA.length;
		
		for (int i = 0; i < LENGTH; i += 6) {
			float x = RANDOM.nextFloat() * 2 - 1;
			float y = RANDOM.nextFloat() * 2 - 1;
			float z = RANDOM.nextFloat() * 2 - 1;
			float sqrt = (float)(1.0 / Math.sqrt(x * x + y * y + z * z));
			float dx = x * sqrt;
			float dy = y * sqrt;
			float dz = z * sqrt;
			
			RANDOM_DATA[i    ] = x;
			RANDOM_DATA[i + 1] = y;
			RANDOM_DATA[i + 2] = z;
			RANDOM_DATA[i + 3] = dx;
			RANDOM_DATA[i + 4] = dy;
			RANDOM_DATA[i + 5] = dz;
		}
		
		// Warmup
		for(int i = 0; i < 600; i += 6) {
			scene.trace(
				RANDOM_DATA[i    ], RANDOM_DATA[i + 1], RANDOM_DATA[i + 2], // xyz
				RANDOM_DATA[i + 3], RANDOM_DATA[i + 4], RANDOM_DATA[i + 5]  // delta xyz
			);
		}
		
		// Test Run
		long time = System.nanoTime();
		for(int i = 0; i < LENGTH; i += 6) {
			scene.trace(
				RANDOM_DATA[i    ], RANDOM_DATA[i + 1], RANDOM_DATA[i + 2], // xyz
				RANDOM_DATA[i + 3], RANDOM_DATA[i + 4], RANDOM_DATA[i + 5]  // delta xyz
			);
		}
		time = System.nanoTime() - time;
		System.out.printf("Took: (%.2f ms) for %d iterations\n", (time / 1000000.0), ITERATIONS);
		System.out.printf("Took: (%.2f ms) per iteration\n", (time / 1000000.0) / (0.0 + ITERATIONS));
		*/
		
		new ProjectEdit()
			.start();
		//test_word_wrap_problem();
//		test_word_break_problem();
	}
	
	
	public static void test_word_break_problem() {
		String[] array = new String[] { "this", "th", "is", "famous", "Word", "break", "b", "r", "e", "a", "k", "br", "bre", "brea", "ak", "problem" };
		String input = "Wordbreakproblem";
		
//		Set<String> set = new TreeSet<>(word_break_problem(input, "", Set.of(array)).toList());
//		set.forEach(System.out::println);
		System.out.println(word_break_problem(input, Set.of(array)));
	}

	public static Stream<String> word_break_problem(String input, String combined, Set<String> dict) {
		return input.length() == 0 ? Stream.of(combined.trim()):dict.stream()
			.filter(input::startsWith)
			.flatMap(word -> word_break_problem(input.substring(word.length()), combined + " " + word, dict));
	}
	
	public static boolean word_break_problem(String input, Set<String> words) {
		return input.length() == 0 ? true:words.stream()
			.filter(input::startsWith)
			.anyMatch(word -> word_break_problem(input.substring(word.length()), words));
	}
	
	
	// https://www.geeksforgeeks.org/word-wrap-problem-dp-19/
	public static void test_word_wrap_problem() {
		String input = "Geeks for Geeks presents word wrap problem";
		int M = 15;
		
		String[] inputArray = input.split(" ");
		
		int[] tmp = new int[inputArray.length * 2 + 1];
		tmp[0] = Integer.MAX_VALUE;
		word_wrap_problem(inputArray, tmp, 0, 1, 0, M);
		
		
		for(int i = 0, j = 0; i < inputArray.length; i++) {
			int count = tmp[i + 1];
			if(count == 0) break;
			
			String str = "";
			for(int k = 0; k < count; k++, j++) str += inputArray[j] + " ";
			System.out.printf("'%-" + M + "s'\n", str.trim());
		}
		
		System.out.println("Total Error: " + tmp[0]);
	}
	
	public static void word_wrap_problem(String[] input, int[] tmp, int index, int line, int error, int M) {
		if(index > input.length) return;
		if(index == input.length) {
			if(tmp[0] > error) {
				tmp[0] = error;
				System.arraycopy(tmp, 1, tmp, input.length + 1, input.length);
			}
			
			return;
		}
		
		int count = -1;
		while(index < input.length) {
			count += input[index++].length() + 1;
			if(count > M) break;
			tmp[line]++;
			tmp[line + 1] = 0;
			word_wrap_problem(input, tmp, index, line + 1, error + (int)Math.pow(M - count, 3), M);
		}
	}
	
//	public static void word_wrap_problem_v4(String[] input, int[] tmp, int index, int line, int error, int M) {
//		if(index > input.length) return;
//		if(index == input.length) {
//			// We are at the end
//			// check with the best and replace if we are better here.
//			if(tmp[0] > error) {
//				tmp[0] = error;
//				//System.out.printf("%sNew best\n", " ".repeat((line-1)<<1));
//				System.arraycopy(tmp, 1, tmp, input.length + 1, input.length);
//			}
//			
//			return;
//		}
//		
//		int count = -1;
//		while(index < input.length) {
//			count += input[index++].length() + 1;
//			if(count > M) break;
//			
//			tmp[line]++;
//			tmp[line + 1] = 0;
//
//			//System.out.printf("%sword: [%s] { count: %d, index: %d, error: %d }\n", " ".repeat((line-1)<<1), input[index - 1], count, index, error + (int)Math.pow(M - count, 3));
//			
//			// Search if we can continue the next line
//			word_wrap_problem_v4(input, tmp, index, line + 1, error + (int)Math.pow(M - count, 3), M);
//		}
//	}
//	
//	public static void word_wrap_problem_v3(String[] input, int[] result, int[] tmp, int index, int line, int error, int M) {
//		if(index > input.length) return;
//		if(index == input.length) {
//			// We are at the end
//			// check with the best and replace if we are better here.
//			if(result[0] > error) {
//				System.out.printf("%sNew best\n", " ".repeat(line<<1));
//				result[0] = error;
//				System.arraycopy(tmp, 0, result, 1, input.length);
//			}
//			
//			return;
//		}
//		
//		int count = -1;
//		for(int i = index, j = 0; i < input.length; i++, j++) {
//			int len = input[i].length() + 1;
//			if(count + len > M) break;
//			count += len;
//			
//			int nextError = error + (int)Math.pow(M - count, 3);
//			tmp[line] = j + 1;
//			tmp[line + 1] = 0;
//			
//			System.out.printf("%sword: [%s] { count: %d, index: %d, error: %d }\n", " ".repeat(line<<1), input[i], count, index, nextError);
//			
//			// Search if we can continue the next line
//			word_wrap_problem_v3(input, result, tmp, i + 1, line + 1, nextError, M);
//		}
//	}
//	
//	public static void word_wrap_problem_v2(String[] input, int[] result, int[] tmp, int index, int line, int error, int M) {
//		if(index > input.length) return;
//		if(index == input.length) {
//			// We are at the end
//			// check with the best and replace if we are better here.
//			if(result[0] > error) {
//				System.out.printf("%sNew best\n", " ".repeat(line<<1));
//				result[0] = error;
//				System.arraycopy(tmp, 0, result, 1, input.length);
//			}
//			
//			return;
//		}
//		
//		int max_usage = 0;
//		int count = -1;
//		for(int i = index; i < input.length; i++, max_usage++) {
//			int len = input[i].length() + 1;
//			if(count + len > M) {
//				break;
//			}
//			
//			count += len;
//		}
//		
//		count = -1;
//		for(int i = 0; i < max_usage; i++) {
//			int idx = index + i;
//			String current = input[idx];
//			count += current.length() + 1;
//			
//			int lineError = (int)Math.pow(M - count, 3);
//			int nextError = error + lineError;
//
//			System.out.printf("%sword: [%s] { count: %d, index: %d, error: %d }\n", " ".repeat(line<<1), current, count, index, nextError);
//			tmp[line] = i + 1;
//			tmp[line + 1] = 0;
//			
//			// Search if we can continue the next line
//			word_wrap_problem_v2(input, result, tmp, idx + 1, line + 1, nextError, M);
//		}
//	}
//	
//	
//	public static void word_wrap_problem_v1(String[] input, int[] result, int[] tmp, int index, int line, int error, int M) {
//		if(index > input.length) return;
//		if(index == input.length) {
//			// We are at the end
//			// check with the best and replace if we are better here.
//			if(result[0] > error) {
//				System.out.printf("%sNew best\n", " ".repeat(line<<1));
//				result[0] = error;
//				System.arraycopy(tmp, 0, result, 1, input.length);
//			}
//			
//			return;
//		}
//		
//		int count = -1;
//		while(count < M && index < input.length) {
//			String current = input[index];
//			int offset = current.length() + 1;
//			
//			if(count + offset > M) {
//				int lineError = (int)Math.pow(M - count, 3);
//				int nextError = error + lineError;
//				
//				System.out.printf("%sword: [%s] { count: %d, index: %d, error: %d }\n", " ".repeat(line<<1), current, count, index, nextError);
//				
//				// Continue to next line without incrementing index
//				word_wrap_problem_v1(input, result, tmp, index, line + 1, nextError, M);
//				break;
//			} else if(count + offset == M) {
//				int nextError = error;
//				System.out.printf("%sword: [%s] { count: %d, index: %d, error: %d }\n", " ".repeat(line<<1), current, count, index, nextError);
//				
//				tmp[line]++;
//				tmp[line + 1] = 0;
//				
//				// Got a perfect line. No error
//				// Continue to next line
//				word_wrap_problem_v1(input, result, tmp, index + 1, line + 1, nextError, M);
//				break;
//			} else {
//				count += offset;
//				index += 1;
//				
//				int lineError = (int)Math.pow(M - count, 3);
//				int nextError = error + lineError;
//
//				System.out.printf("%sword: [%s] { count: %d, index: %d, error: %d }\n", " ".repeat(line<<1), current, count, index, nextError);
//				tmp[line]++;
//				tmp[line + 1] = 0;
//				
//				// Search if we can continue the next line
//				word_wrap_problem_v1(input, result, tmp, index, line + 1, nextError, M);
//			}
//		}
//	}
	
//	public static Set<String> recurse(String input, String combined, String[] dict) {
//		Set<String> results = new LinkedHashSet<>();
//		for(String word : dict) {
//			if(input.startsWith(word)) {
//				results.addAll(recurse(input.substring(word.length()), combined + " " + word + " ", dict));
//			}
//		}
//		
//		if(input.length() == 0) {
//			return Set.of(combined.replaceAll("\\s+", " ").trim());
//		} else {
//			//recurse(input.substring(1), combined + input.charAt(0), dict);
//		}
//		
//		return results;
//	}
	
//	public static int index = 0;
//	public static boolean recurse(String input, String[] dict) {
//		for(String word : dict) {
//			if(input.startsWith(word)) {
//				if(recurse(input.substring(word.length()), dict)) {
//					return true;
//				}
//			}
//		}
//		
//		return input.length() == 0;
//	}
}
