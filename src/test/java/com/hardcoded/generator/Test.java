package com.hardcoded.generator;

public class Test {
//	public static void main(String[] args) {
//		final int length = 1000000;
//		final int times = 100000;
//		final int reads = length / 8;
//		final double iterations = (reads + 0.0) * (times + 0.0);
//		byte[] garbled = new byte[length];
//		Random random = new Random();
//		random.nextBytes(garbled);
//		
//		// ByteBuf_Optimized buf_0 = new ByteBuf_Optimized(garbled);
//		ByteBuffer buf_0 = ByteBuffer.wrap(garbled);
//		ByteBuf buf_1 = ByteBuf.direct(garbled);
//		
//		//buf_0.mark();
//		
//		// Warmup
//		for(int i = 0; i < reads; i++) {
//			buf_1.readLong();
//			//buf_0.getLong();
//		}
//		
//		// Benchmark
//		long now = System.nanoTime();
//		for(int i = 0; i < times; i++) {
//			buf_1.readerIndex(0);
//			//buf_0.reset();
//			for(int j = 0; j < reads; j++) {
//				buf_1.readLong();
//				//buf_0.getLong();
//			}
//		}
//		long time = System.nanoTime() - now;
//		
//		System.out.printf("Took: %.4f ms\n", (time / iterations) * 10000.0);
//		System.out.println(time);
//	}
	
//	public static void main(String[] args) {
//		final int length = 1000000;
//		final int times = 100000;
//		final int reads = length / 8;
//		final double iterations = (reads + 0.0) * (times + 0.0);
//		byte[] garbled = new byte[length];
//		Random random = new Random();
//		random.nextBytes(garbled);
//		
//		// ByteBuf_Optimized buf_0 = new ByteBuf_Optimized(garbled);
//		ByteBuffer buf_0 = ByteBuffer.wrap(garbled);
//		ByteBuf buf_1 = ByteBuf.direct(garbled);
//		
//		buf_0.mark();
//		
//		// Warmup
//		for(int i = 0; i < reads; i++) {
//			buf_0.getLong();
//		}
//		
//		// Benchmark
//		long now = System.nanoTime();
//		for(int i = 0; i < times; i++) {
//			buf_0.reset();
//			for(int j = 0; j < reads; j++) {
//				buf_0.getLong();
//			}
//		}
//		long time = System.nanoTime() - now;
//		
//		System.out.printf("Took: %.4f ms\n", (time / iterations) * 10000.0);
//		System.out.println(time);
//	}
}
