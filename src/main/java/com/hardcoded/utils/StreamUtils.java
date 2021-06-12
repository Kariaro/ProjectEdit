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
		try {
			return try_decompress_deflate(array, 3);
		} catch(DataFormatException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static byte[] try_decompress_deflate(byte[] array, final int retries) throws DataFormatException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		Inflater inflater = new Inflater();
		byte[] buffer = new byte[8192];
		int readBytes;
		
		for(int i = 0; i < retries; i++) {
			try {
				inflater.setInput(array);
				while((readBytes = inflater.inflate(buffer)) > 0) {
					bs.write(buffer, 0, readBytes);
				}
				
				inflater.end();
				
				return bs.toByteArray();
			} catch(DataFormatException e) {
				inflater.reset();
				bs.reset();
				
				if(i == retries - 1) {
					throw e;
				}
			}
		}
		
		return null;
	}
}
