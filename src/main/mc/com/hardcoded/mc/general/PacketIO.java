package com.hardcoded.mc.general;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.hardcoded.mc.general.nbt.NBTBase;

public class PacketIO {
	private final ByteBuf buffer;
	
	public PacketIO(ByteBuf buffer) {
		this.buffer = buffer;
	}
	
	public PacketIO writeVarInt(int value) {
		do {
			byte temp = (byte)(value & 0b01111111);
			value >>>= 7;
			if(value != 0) {
				temp |= 0b10000000;
			}
			
			buffer.writeByte(temp);
		} while(value != 0);
		
		return this;
	}
	
	public PacketIO writeNBT(NBTBase nbt) {
		nbt.write(buffer, 0);
		return this;
	}
	
	public int readVarInt() {
		int numRead = 0;
		int result = 0;
		byte read;
		
		do {
			read = buffer.readByte();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));
			
			numRead++;
			if(numRead > 5) {
				throw new RuntimeException("VarInt is too big");
			}
		} while((read & 0b10000000) != 0);
		
		return result;
	}
	
//	public PacketIO markReaderIndex() {
//		buffer.markReaderIndex();
//		return this;
//	}
//	
//	public PacketIO resetReaderIndex() {
//		buffer.resetReaderIndex();
//		return this;
//	}
	
	public PacketIO writeDouble(double value) {
		buffer.writeDouble(value);
		return this;
	}
	
	public PacketIO writeAngle(float value) {
		writeByte((byte)(value * 256.0f / 360.0f));
		return this;
	}
	
	public PacketIO writeFloat(float value) {
		buffer.writeFloat(value);
		return this;
	}
	
//	public PacketIO markWriterIndex() {
//		buffer.markWriterIndex();
//		return this;
//	}
//	
//	public PacketIO resetWriterIndex() {
//		buffer.resetWriterIndex();
//		return this;
//	}
	
	public UUID readUUID() {
		return new UUID(readLong(), readLong());
	}
	
	public PacketIO writeUUID(UUID uuid) {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
		return this;
	}
	
	public PacketIO writeShort(int value) {
		buffer.writeShort(value);
		return this;
	}
	
	public PacketIO writeInt(int value) {
		buffer.writeInt(value);
		return this;
	}
	
	public int readerIndex() {
		return buffer.readerIndex();
	}
	
	public PacketIO readerIndex(int index) {
		buffer.readerIndex(index);
		return this;
	}
	
	public PacketIO writeBoolean(boolean value) {
		buffer.writeBoolean(value);
		return this;
	}
	
	public ByteBuf getBuffer() {
		return buffer;
	}
	
	public PacketIO writeUnsignedByte(int value) {
		buffer.writeByte(value);
		return this;
	}
	
	public int writerIndex() {
		return buffer.writerIndex();
	}
	
	public PacketIO writerIndex(int index) {
		buffer.writerIndex(index);
		return this;
	}
	
	public int readableBytes() {
		return buffer.readableBytes();
	}
	
	public boolean hasReadableBytes() {
		return buffer.readableBytes() > 0;
	}
	
	public PacketIO readBytes(byte[] bytes, int offset, int length) {
		buffer.readBytes(bytes, offset, length);
		return this;
	}
	
	public PacketIO readBytes(byte[] bytes) {
		buffer.readBytes(bytes);
		return this;
	}
	
	public PacketIO writeString(String str) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		writeVarInt(bytes.length);
		buffer.writeBytes(bytes);
		return this;
	}
	
	public PacketIO writeString(String str, int max_length) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		if(bytes.length > max_length) {
			// Too big
			return this;
		}
		
		writeVarInt(bytes.length);
		buffer.writeBytes(bytes);
		return this;
	}
	
	public void writeBytes(byte[] bytes) {
		buffer.writeBytes(bytes);
	}
	
	public void writeBytes(ByteBuf src) {
		buffer.writeBytes(src);
	}
	
	public byte[] readBytes(int length) {
		byte[] bytes = new byte[length];
		buffer.readBytes(bytes);
		return bytes;
	}
	
//	public String readString(int max_length) {
//		int length = readVarInt();
//		
//		if(length > max_length * 4) {
//			//throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + (length * 4) + ")"); 
//			return null;
//		}
//		if(length < 0) {
//			//throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
//			return null;
//		}
//		
//		String string = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
//		buffer.readerIndex(buffer.readerIndex() + length);
//		
//		if(string.length() > max_length) {
//			//throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + i + ")"); 
//			return null;
//		}
//		
//		return string;
//	}
	
	public PacketIO writeLong(long l) {
		buffer.writeLong(l);
		return this;
	}
	
	public int readUnsignedShort() {
		return buffer.readUnsignedShort();
	}
	
	public boolean readBoolean() {
		return buffer.readBoolean();
	}
	
	public int readUnsignedByte() {
		return buffer.readUnsignedByte();
	}
	
	public PacketIO writeByte(int value) {
		buffer.writeByte(value);
		return this;
	}
	
	public int readInt() {
		return buffer.readInt();
	}
	
	public short readShort() {
		return buffer.readShort();
	}
	
	public long readLong() {
		return buffer.readLong();
	}
	
	public byte readByte() {
		return buffer.readByte();
	}
	
	public double readDouble() {
		return buffer.readDouble();
	}
	
	public float readFloat() {
		return buffer.readFloat();
	}
}
