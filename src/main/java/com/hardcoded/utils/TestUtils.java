package com.hardcoded.utils;

public class TestUtils {
	public static void debugPrint(String name, byte[] data, int offset) {
		int len = data.length - offset;
		StringBuilder sb = new StringBuilder();
		
		if(name.isEmpty()) {
			sb.append("######################");
		} else {
			sb.append("########### [").append(name).append("] ###########");
		}
		
		
		if(len > 255) len = 255;
		
		sb.append(": len=").append(len).append(", idx=").append(offset).append("\n");
		
		for(int i = 0; i < len; i++) sb.append(String.format("%02x ", ((int)data[i + offset]) & 0xff));
		sb.append("\n");
		for(int i = 0; i < len; i++) {
			char c = (char)(((int)data[i + offset]) & 0xff);
			sb.append(String.format("%2s ", (Character.isWhitespace(c) || Character.isISOControl(c) ? ".":c)));
		}
		sb.append("\n");
		if(name.isEmpty()) {
			sb.append("######################");
		} else {
			sb.append("########### [").append(name).append("] ###########");
		}
		
		System.out.println(sb.toString());
	}
}
