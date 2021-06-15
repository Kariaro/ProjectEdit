package com.hardcoded.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Time utils handler.
 * 
 * @author HardCoded
 */
public class TimerUtils {
	private static final AtomicInteger index = new AtomicInteger(0);
	private static final long[] buffer = new long[8196];
	
	private static boolean average;
	private static double total;
	private static long times;
	
	public static final long begin() {
		return buffer[index.getAndIncrement()] = System.nanoTime();
	}
	
	public static final long end() {
		long result = System.nanoTime() - buffer[index.getAndDecrement() - 1];
		if(average) {
			total += result;
			times ++;
		}
		
		return result;
	}
	
	public static final void beginAverage() {
		average = true;
		times = 0;
		total = 0;
	}
	
	public static final double endAverage() {
		average = false;
		double result = (total / (times + 0.0));
		total = 0;
		return result;
	}
	
	public static final long getTimes() {
		return times;
	}
}
