package com.hardcoded.mc.general;

/**
 * This class provies methods to read and write to byte arrays.
 * 
 * @author HardCoded
 *
 */
public class ByteBuf {
	private byte[] buffer;
	private int readerIndex;
	private int writerIndex;
	
	/**
	 * Construct a new byte buffer with a set capacity.
	 * @param capacity the capacity of this buffer
	 */
	public ByteBuf(int capacity) {
		this.buffer = new byte[capacity];
	}
	
	/**
	 * Construct a new byte buffer with the content of byte array .
	 * @param buffer the source array
	 */
	public ByteBuf(byte[] buffer) {
		this.buffer = buffer.clone();
	}
	
	/**
	 * @return the direct byte array stored inside this ByteBuf class
	 */
	public byte[] getBuffer() {
		return buffer;
	}
	
	// OPTION METHODS
	
	public int writerIndex() {
		return writerIndex;
	}
	
	public int readerIndex() {
		return readerIndex;
	}
	
	public int writerIndex(int index) {
		int tmp = writerIndex;
		this.writerIndex = index;
		return tmp;
	}
	
	public int readerIndex(int index) {
		int tmp = readerIndex;
		this.readerIndex = index;
		return tmp;
	}
	
	public int readableBytes() {
		return buffer.length - readerIndex;
	}
	
	// PRIVATE METHODS
	
	private long readValue(final int offset, final int length) {
		long result = 0;
		
		final int ofs = offset + length - 1;
		for(int i = 0; i < length; i++) {
			long val = Byte.toUnsignedLong(buffer[ofs - i]);
			result |= (val << (i * 8L));
		}
		
		return result;
	}
	
	private void writeValue(long number, final int offset, final int length) {
		number &= (~((-1L) << (length)));
		
		final int ofs = offset + length - 1;
		for(int i = 0; i < length; i++) {
			buffer[ofs - i] = (byte)((number >>> (i * 8L)) & 0xff);
		}
	}
	
	// PUBLIC METHODS
	
	public void writeBytes(byte[] array) {
		System.arraycopy(array, 0, buffer, writerIndex, array.length);
		writerIndex += array.length;
	}
	
	public void writeBytes(ByteBuf buf) {
		System.arraycopy(buf.buffer, 0, buffer, writerIndex, buf.buffer.length);
		writerIndex += buf.buffer.length;
	}
	
	public byte[] readBytes(byte[] array) {
		System.arraycopy(buffer, readerIndex, array, 0, array.length);
		readerIndex += array.length;
		return array;
	}
	
	public byte[] readBytes(int length) {
		byte[] array = new byte[length];
		System.arraycopy(buffer, readerIndex, array, 0, array.length);
		readerIndex += array.length;
		return array;
	}
	
	public byte[] readBytes(byte[] array, int offset, int length) {
		System.arraycopy(buffer, readerIndex, array, offset, length);
		readerIndex += length;
		return array;
	}
	
	public boolean readBoolean() {
		return readValue((readerIndex += 1) - 1, 1) != 0;
	}
	
	public void writeBoolean(boolean value) {
		writeValue(value ? 1:0, (writerIndex += 1) - 1, 1);
	}
	
	
	public byte readByte() {
		return buffer[(readerIndex += 1) - 1];
	}
	
	public void writeByte(long value) {
		buffer[(writerIndex += 1) - 1] = (byte)value;
	}
	
	public int readUnsignedByte() {
		return (int)(buffer[(readerIndex += 1) - 1] & 0xff);
	}
	
	
	public short readShort() {
		return (short)readValue((readerIndex += 2) - 2, 2);
	}
	
	public void writeShort(long value) {
		writeValue(value, (writerIndex += 2) - 2, 2);
	}
	
	public int readUnsignedShort() {
		return (int)(readValue((readerIndex += 2) - 2, 2) & 0xffff);
	}
	
	
	public int readInt() {
		return (int)readValue((readerIndex += 4) - 4, 4);
	}
	
	public void writeInt(long value) {
		writeValue(value, (writerIndex += 4) - 4, 4);
	}
	
	public long readUnsignedInt() {
		return (long)(readValue((readerIndex += 4) - 4, 4) & 0xffffffffL);
	}
	
	
	public long readLong() {
		return readValue((readerIndex += 8) - 8, 8);
	}
	
	public void writeLong(long value) {
		writeValue(value, (writerIndex += 8) - 8, 8);
	}
	
	
	public double readDouble() {
		return Double.longBitsToDouble(readValue((readerIndex += 8) - 8, 8));
	}
	
	public void writeDouble(double value) {
		writeValue(Double.doubleToRawLongBits(value), (writerIndex += 8) - 8, 8);
	}
	
	
	public float readFloat() {
		return Float.intBitsToFloat((int)readValue((readerIndex += 4) - 4, 4));
	}
	
	public void writeFloat(float value) {
		writeValue(Float.floatToRawIntBits(value), (writerIndex += 4) - 4, 4);
	}
	
}
