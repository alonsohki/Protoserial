package com.conditionracer.protoserial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerializationMethods
{
	public static void RegisterMethods ( Manager manager )
	{
		manager.AddMethod(Short.class, new ISerializationMethod ()
		{
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Short(reader.readShort());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				DataOutputStream writer = new DataOutputStream(to);
				writer.writeShort((Short)obj);
			}
		});
		
		manager.AddMethod(Integer.class, new ISerializationMethod ()
		{
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Integer(reader.readInt());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				DataOutputStream writer = new DataOutputStream(to);
				writer.writeInt((Integer)obj);
			}
		});
		
		manager.AddMethod(Long.class, new ISerializationMethod ()
		{
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Long(reader.readLong());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				DataOutputStream writer = new DataOutputStream(to);
				writer.writeLong((Long)obj);
			}
		});
		
		manager.AddMethod(Float.class, new ISerializationMethod ()
		{
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Float(reader.readFloat());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				DataOutputStream writer = new DataOutputStream(to);
				writer.writeFloat((Float)obj);
			}
		});
		
		
		manager.AddMethod(Double.class, new ISerializationMethod ()
		{
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Double(reader.readDouble());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				DataOutputStream writer = new DataOutputStream(to);
				writer.writeDouble((Double)obj);
			}
		});
		
		manager.AddMethod(String.class, new ISerializationMethod ()
		{
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new String();
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				DataOutputStream writer = new DataOutputStream(to);
			}
		});
	}
}
