package com.conditionracer.protoserial;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BigEndianDataInputStream extends InputStream implements DataInput
{
	private InputStream mInner;
	
	public BigEndianDataInputStream ( InputStream inner )
	{
		mInner = inner;
	}
	
	@Override
	public boolean readBoolean() throws IOException
	{
		return readByte() == 1;
	}

	@Override
	public byte readByte() throws IOException
	{
		byte[] b = new byte[1];
		mInner.read(b);
		return b[0];
	}

	@Override
	public char readChar() throws IOException
	{
		return (char)readByte();
	}

	@Override
	public double readDouble() throws IOException
	{
		byte[] b = new byte[8];
		mInner.read(b);
		return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getDouble();
	}

	@Override
	public float readFloat() throws IOException
	{
		byte[] b = new byte[4];
		mInner.read(b);
		return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}

	@Override
	public void readFully(byte[] arg0) throws IOException
	{
		mInner.read(arg0);
	}

	@Override
	public void readFully(byte[] arg0, int arg1, int arg2) throws IOException
	{
		mInner.read(arg0, arg1, arg2);
	}

	@Override
	public int readInt() throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		mInner.read(bb.array(), 0, 4);
		return bb.getInt();
	}

	@Override
	public String readLine() throws IOException
	{
		return null;
	}

	@Override
	public long readLong() throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		mInner.read(bb.array(), 0, 8);
		return bb.getLong();
	}

	@Override
	public short readShort() throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		mInner.read(bb.array(), 0, 2);
		return bb.getShort();
	}

	@Override
	public String readUTF() throws IOException
	{
		int length = readUnsignedShort();
		if ( length > 0 )
		{
			byte[] b = new byte[length];
			mInner.read(b);
			return new String(b, "UTF-8");
		}
		else
		{
			return new String();
		}
	}

	@Override
	public int readUnsignedByte() throws IOException
	{
		return readByte();
	}

	@Override
	public int readUnsignedShort() throws IOException
	{
		int b = readByte();
		int value = b & 0x7F;
		if ( (b & 0x80) == 0x80 )
		{
			b = readByte();
			value |= b << 7;
		}
		return value;
	}
	
	public int readUnsingedInt() throws IOException
	{
		long b = readByte();
		long value = b & 0x7F;
		int n = 7;
		
		while ( ( b & 0x80 ) == 0x80 )
		{
			b = readByte();
			value |= (b & 0x7F) << n;
			n += 7;
		}
		
		return (int)value;
	}

	public long readUnsignedLong() throws IOException
	{
		long b = readByte();
		long value = b & 0x7F;
		int n = 7;
		
		while ( ( b & 0x80 ) == 0x80 )
		{
			b = readByte();
			value |= (b & 0x7F) << n;
			n += 7;
		}
		
		return value;
	}
	
	@Override
	public int skipBytes(int arg0) throws IOException
	{
		return (int) mInner.skip(arg0);
	}

	@Override
	public int read() throws IOException
	{
		return mInner.read();
	}

}
