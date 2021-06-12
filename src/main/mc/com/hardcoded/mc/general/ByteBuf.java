package com.hardcoded.mc.general;

/**
 * This {@code ByteBuf} class is used to read from and write to arrays.
 * 
 * <p>To instantiate this class use one of the following static methods.
 * <br>{@link #direct(byte[])} to create a direct buffer. Any modification to this {@code ByteBuf} will modify the source array.
 * <br>{@link #copy(byte[])} to create a {@code ByteBuf} with the contents of another byte array.
 * <br>{@link #allocate(int)} to create a {@code ByteBuf} with a set size.
 * <br>{@link #readOnly(byte[])} to create a {@code ByteBuf} with direct access to a byte array but without the ability to modify that array.
 * 
 * @author HardCoded
 */
public class ByteBuf {
	private byte[] buffer;
	private int readerIndex;
	private int writerIndex;
	
	/**
	 * Construct a new byte buffer with a set capacity.
	 * @param capacity the capacity of this buffer
	 */
	private ByteBuf(int capacity) {
		this.buffer = new byte[capacity];
	}
	
	/**
	 * Construct a new {@code ByteBuf} that has direct access to the passed byte array.
	 * @param buffer the byte array
	 */
	private ByteBuf(byte[] buffer) {
		this.buffer = buffer;
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
	
	/**
	 * If this {@code ByteBuf} is readOnly then any calls to any
	 * write method will result in a {@code UnsupportedOperationException}.
	 * @return {@code true} if this {@code ByteBuf} is read only
	 */
	public boolean isReadOnly() {
		return false;
	}
	
	
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
	
	public byte[] readBytes(int offset, int length) {
		byte[] array = new byte[length];
		System.arraycopy(buffer, offset, array, 0, array.length);
		return array;
	}
	
	public byte[] readBytes(byte[] array, int offset, int length) {
		System.arraycopy(buffer, readerIndex, array, offset, length);
		readerIndex += length;
		return array;
	}
	
	
	public boolean readBoolean() {
		return buffer[(readerIndex += 1) - 1] != 0;
	}
	
	public void writeBoolean(boolean value) {
		buffer[(writerIndex += 1) - 1] = (byte)(value ? 1:0);
	}
	
	public byte readByte() {
		return buffer[(readerIndex += 1) - 1];
	}
	
	public int readUnsignedByte() {
		return (int)(buffer[(readerIndex += 1) - 1] & 0xff);
	}
	
	public int readUnsignedByte(int offset) {
		return (int)(buffer[offset] & 0xff);
	}
	
	public void writeByte(long value) {
		buffer[(writerIndex += 1) - 1] = (byte)value;
	}
	
	
	public short readShort() {
		final int offset = (readerIndex += 2);
		return (short)((((long)buffer[offset - 1]) & 0xffL)
			| ((((long)buffer[offset - 2]) & 0xffL) << 8L));
	}
	
	public int readUnsignedShort() {
		final int offset = (readerIndex += 2);
		return (int)((((long)buffer[offset - 1]) & 0xffL)
			| ((((long)buffer[offset - 2]) & 0xffL) << 8L)) & 0xffff;
	}
	
	public void writeShort(long value) {
		final int offset = (writerIndex += 2);
		buffer[offset - 1] = (byte)value;
		buffer[offset - 2] = (byte)(value >>> 8L);
	}
	
	
	public int readInt() {
		final int offset = (readerIndex += 4);
		return (int)((((long)buffer[offset - 1]) & 0xffL)
			| ((((long)buffer[offset - 2]) & 0xffL) << 8L)
			| ((((long)buffer[offset - 3]) & 0xffL) << 16L)
			| ((((long)buffer[offset - 4]) & 0xffL) << 24L));
	}
	
	public int readInt(int offset) {
		return (int)((((long)buffer[offset + 3]) & 0xffL)
			| ((((long)buffer[offset + 2]) & 0xffL) << 8L)
			| ((((long)buffer[offset + 1]) & 0xffL) << 16L)
			| ((((long)buffer[offset]) & 0xffL) << 24L));
	}
	
	public long readUnsignedInt() {
		final int offset = (readerIndex += 4);
		return ((((long)buffer[offset - 1]) & 0xffL)
			| ((((long)buffer[offset - 2]) & 0xffL) << 8L)
			| ((((long)buffer[offset - 3]) & 0xffL) << 16L)
			| ((((long)buffer[offset - 4]) & 0xffL) << 24L)) & 0xffffffffL;
	}
	
	public void writeInt(long value) {
		final int offset = (writerIndex += 4);
		buffer[offset - 1] = (byte)value;
		buffer[offset - 2] = (byte)(value >>> 8L);
		buffer[offset - 3] = (byte)(value >>> 16L);
		buffer[offset - 4] = (byte)(value >>> 24L);
	}
	
	
	public long readLong() {
		final int offset = (readerIndex += 8);
		return (((long)buffer[offset - 1]) & 0xffL)
			| ((((long)buffer[offset - 2]) & 0xffL) << 8L)
			| ((((long)buffer[offset - 3]) & 0xffL) << 16L)
			| ((((long)buffer[offset - 4]) & 0xffL) << 24L)
			| ((((long)buffer[offset - 5]) & 0xffL) << 32L)
			| ((((long)buffer[offset - 6]) & 0xffL) << 40L)
			| ((((long)buffer[offset - 7]) & 0xffL) << 48L)
			| ((((long)buffer[offset - 8]) & 0xffL) << 56L);
	}
	
	public void writeLong(long value) {
		final int offset = (writerIndex += 8);
		buffer[offset - 1] = (byte)value;
		buffer[offset - 2] = (byte)(value >>> 8L);
		buffer[offset - 3] = (byte)(value >>> 16L);
		buffer[offset - 4] = (byte)(value >>> 24L);
		buffer[offset - 5] = (byte)(value >>> 32L);
		buffer[offset - 6] = (byte)(value >>> 40L);
		buffer[offset - 7] = (byte)(value >>> 48L);
		buffer[offset - 8] = (byte)(value >>> 56L);
	}
	
	
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}
	
	public void writeFloat(float value) {
		writeInt(Float.floatToRawIntBits(value));
	}
	
	
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}
	
	public void writeDouble(double value) {
		writeLong(Double.doubleToRawLongBits(value));
	}
	
	/**
	 * Construct a new {@code ByteBuf} that has direct access to a byte array.
	 * That means that when you modify this buffer with a write command the
	 * byte array will also change.
	 * 
	 * @param array the array
	 * @return a {@code ByteBuf} with direct access to the specified byte array
	 */
	public static ByteBuf direct(byte[] array) {
		return new ByteBuf(array);
	}
	
	/**
	 * Construct a new {@code ByteBuf} that contains the content of a byte array.
	 * Any modifications to the {@code ByteBuf} will not change the passed byte
	 * array.
	 * 
	 * @param array the source array
	 * @return a new {@code ByteBuf}
	 */
	public static ByteBuf copy(byte[] array) {
		return new ByteBuf(array.clone());
	}
	
	/**
	 * Construct a new {@code ByteBuf} with a specified capacity.
	 * @param capacity the amount of bytes this {@code ByteBuf} should contain.
	 * @return a new {@code ByteBuf}
	 */
	public static ByteBuf allocate(int capacity) {
		return new ByteBuf(capacity);
	}
	
	/**
	 * Construct a new {@code ByteBuf} that has direct access to a byte array.
	 * This method allows a creation of a {@code ByteBuf} with direct access to
	 * a byte array without allowing any modifications to the underlying array.
	 * 
	 * <p><b>Note:</b> Any calls to any write method will throw a {@code UnsupportedOperationException}.
	 * 
	 * @param array the array
	 * @return a {@code ByteBuf} with direct access to the specified byte array
	 */
	public static ByteBuf readOnly(byte[] array) {
		return new ByteBuf(array) {
			@Override
			public boolean isReadOnly() {
				return true;
			}
			
			@Override
			public void writeBoolean(boolean value) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeByte(long value) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeBytes(byte[] array) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeBytes(ByteBuf buf) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeDouble(double value) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeFloat(float value) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeInt(long value) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeLong(long value) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void writeShort(long value) {
				throw new UnsupportedOperationException();
			}
		};
	}
}
