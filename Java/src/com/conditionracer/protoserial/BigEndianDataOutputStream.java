package com.conditionracer.protoserial;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BigEndianDataOutputStream extends OutputStream implements DataOutput
{
	private OutputStream mInner;
	
	public BigEndianDataOutputStream ( OutputStream inner )
	{
		mInner = inner;
	}
	
	@Override
	public void write(int arg0) throws IOException
	{
		mInner.write(arg0);
	}

	@Override
	public void write(byte[] arg0) throws IOException
	{
		mInner.write(arg0);
	}

	@Override
	public void write(byte[] arg0, int arg1, int arg2) throws IOException
	{
		mInner.write(arg0, arg1, arg2);
		
	}

	@Override
	public void writeBoolean(boolean arg0) throws IOException
	{
		mInner.write(arg0 ? 1 : 0);
	}

	@Override
	public void writeByte(int arg0) throws IOException
	{
		mInner.write(arg0);
	}

	@Override
	public void writeBytes(String arg0) throws IOException
	{
	}

	@Override
	public void writeChar(int arg0) throws IOException
	{
		writeByte ( arg0 );
	}

	@Override
	public void writeChars(String arg0) throws IOException
	{
	}

	@Override
	public void writeDouble(double arg0) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(arg0);
		mInner.write(bb.array());
	}

	@Override
	public void writeFloat(float arg0) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(arg0);
		mInner.write(bb.array());
	}

	@Override
	public void writeInt(int arg0) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(arg0);
		mInner.write(bb.array());
	}

	@Override
	public void writeLong(long arg0) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(arg0);
		mInner.write(bb.array());
	}

	@Override
	public void writeShort(int arg0) throws IOException
	{
		ByteBuffer bb = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short)arg0);
		mInner.write(bb.array());
	}

	@Override
	public void writeUTF(String arg0) throws IOException
	{
		int length = arg0.length();
		writeUnsignedShort(length);
		
		if ( length > 0 )
		{
			byte[] b = arg0.getBytes("UTF-8");
			mInner.write(b);
		}
	}

	public void writeUnsignedShort ( int value ) throws IOException
	{
		writeUnsignedLong(value);
	}
	
	public void writeUnsignedInt ( long value ) throws IOException
	{
		writeUnsignedLong(value);
	}
	
	public void writeUnsignedLong ( long value ) throws IOException
	{
		long mask = 0x7F;
		mask = ~mask;
		
        do
        {
            long b = (value & mask) != 0 ? 0x80 : 0;
            b |= value & 0x7F;
            mInner.write((int)(b & 0xFF));
            value >>>= 7;
        }
        while (value != 0);
	}
}
