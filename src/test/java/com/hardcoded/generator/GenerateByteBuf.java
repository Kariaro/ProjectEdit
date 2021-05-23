package com.hardcoded.generator;

/**
 * This class was used to generate the read and write functions inside {@link ByteBuf}.
 * 
 * @author HardCoded
 */
class GenerateByteBuf {
	static final String generate_read_code(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("final int offset = (readerIndex += ").append(size).append(");\n");
		sb.append("return ");
		for(int k = 0; k < size; k++) {
			if(k == 0) {
				sb.append("(((long)buffer[offset - ").append(k + 1).append("]) & 0xffL)");
				sb.append("\n");
			} else if(k < size - 1) {
				sb.append("| ((((long)buffer[offset - ").append(k + 1).append("]) & 0xffL)");
				sb.append(" << ").append(k * 8).append("L)\n");
			} else {
				sb.append("| ((((long)buffer[offset - ").append(k + 1).append("]) & 0xffL)");
				sb.append(" << ").append(k * 8).append("L);\n");
			}
		}
		return sb.toString();
	}
	
	static final String generate_write_code(int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("final int offset = (writerIndex += ").append(size).append(");\n");
		for(int k = 0; k < size; k++) {
			if(k == 0) {
				sb.append("buffer[offset - ").append(k + 1).append("] = (byte)value;\n");
			} else {
				sb.append("buffer[offset - ").append(k + 1).append("] = (byte)(value >>> ").append(k * 8).append("L);\n");
			}
		}
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		System.out.println(generate_write_code(8));
//	}
}
