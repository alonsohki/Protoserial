package com.conditionracer.protoserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings("resource")
public class VarIntSerializer
{
	
	static int ReadUInt16 ( InputStream from ) throws IOException
	{
		BigEndianDataInputStream reader = new BigEndianDataInputStream ( from );
		return reader.readUnsignedShort();
	}
	
	static int ReadUInt32 ( InputStream from ) throws IOException
	{
		BigEndianDataInputStream reader = new BigEndianDataInputStream ( from );
		return reader.readUnsingedInt();
	}
	
	static long ReadUInt64 ( InputStream from ) throws IOException
	{
		BigEndianDataInputStream reader = new BigEndianDataInputStream ( from );
		return reader.readUnsignedLong();
	}
	
	static void WriteUInt16 ( int value, OutputStream to ) throws IOException
	{
		BigEndianDataOutputStream writer = new BigEndianDataOutputStream ( to );
		writer.writeUnsignedShort(value);
	}

	static void WriteUInt32 ( long value, OutputStream to ) throws IOException
	{
		BigEndianDataOutputStream writer = new BigEndianDataOutputStream ( to );
		writer.writeUnsignedInt(value);
	}
	
	static void WriteUInt64 ( long value, OutputStream to ) throws IOException
	{
		BigEndianDataOutputStream writer = new BigEndianDataOutputStream ( to );
		writer.writeUnsignedLong(value);
	}
}
