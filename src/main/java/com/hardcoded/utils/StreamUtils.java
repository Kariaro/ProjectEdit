package com.hardcoded.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

public class StreamUtils {
	
	public static byte[] decompress_gzip(byte[] array) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		
		try(GZIPInputStream stream = new GZIPInputStream(new ByteArrayInputStream(array))) {
			byte[] buffer = new byte[65536];
			
			while(stream.available() != 0) {
				int readBytes = stream.read(buffer);
				if(readBytes > 0)
					bs.write(buffer, 0, readBytes);
			}
		} catch(IOException e) {
			return null;
		}
		
		return bs.toByteArray();
	}
	
	public static byte[] decompress_deflate(byte[] array) {
		byte[] output = new byte[0x100000];
		Inflater inflater = new Inflater();
		inflater.setInput(array);
		try {
			int resultLength = inflater.inflate(output);
			inflater.end();
			byte[] result = new byte[resultLength];
			System.arraycopy(output, 0, result, 0, resultLength);
			return result;
		} catch(DataFormatException e) {
			
		}
		
		return null;
	}
}
