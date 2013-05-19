package com.conditionracer.protoserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.conditionracer.protoserial.Manager.MethodType;

public class SerializationMethods
{
	@SuppressWarnings("resource")
	public static void RegisterMethods ( Manager manager ) throws SerializationMethodCollisionException
	{
		// Int16
		ISerializationMethod shortMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 10; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Short(reader.readShort());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeShort((Short)obj);
			}
		};
		
		// UInt16
		ISerializationMethod unsignedShortMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 11; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Short((short) reader.readUnsignedShort());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeUnsignedShort((Short)obj);
			}
		};

		// Int32
		ISerializationMethod integerMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 12; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Integer(reader.readInt());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeInt((Integer)obj);
			}
		};
		
		// UInt32
		ISerializationMethod unsignedIntegerMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 13; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Integer(reader.readUnsingedInt());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeUnsignedInt((Integer)obj);
			}
		};
		
		// Int64
		ISerializationMethod longMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 14; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Long(reader.readLong());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeLong((Long)obj);
			}
		};
		
		// UInt64
		ISerializationMethod unsignedLongMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 15; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Long(reader.readUnsignedLong());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeUnsignedLong((Long)obj);
			}
		};

		// Float
		ISerializationMethod floatMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 16; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Float(reader.readFloat());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeFloat((Float)obj);
			}
		};
		
		// Double
		ISerializationMethod doubleMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 17; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return new Double(reader.readDouble());
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeDouble((Double)obj);
			}
		};
		
		// String
		ISerializationMethod stringMethod = new ISerializationMethod ()
		{
			@Override public int GetMethodID () { return 18; }
			
			@Override
			public Object Read(InputStream from) throws IOException
			{
				BigEndianDataInputStream reader = new BigEndianDataInputStream(from);
				return reader.readUTF();
			}

			@Override
			public void Write(Object obj, OutputStream to) throws IOException
			{
				BigEndianDataOutputStream writer = new BigEndianDataOutputStream(to);
				writer.writeUTF((String)obj);
			}
		};
		
		manager.AddMethod(Short.class,   MethodType.TYPE_SIGNED,   shortMethod);
		manager.AddMethod(Short.class,   MethodType.TYPE_UNSIGNED, unsignedShortMethod);
		manager.AddMethod(Integer.class, MethodType.TYPE_SIGNED,   integerMethod);
		manager.AddMethod(Integer.class, MethodType.TYPE_UNSIGNED, unsignedIntegerMethod);
		manager.AddMethod(Long.class,    MethodType.TYPE_SIGNED,   longMethod);
		manager.AddMethod(Long.class,    MethodType.TYPE_UNSIGNED, unsignedLongMethod);
		manager.AddMethod(Float.class,   MethodType.TYPE_SIGNED,   floatMethod);
		manager.AddMethod(Double.class,  MethodType.TYPE_SIGNED,   doubleMethod);
		manager.AddMethod(String.class,  MethodType.TYPE_SIGNED,   stringMethod);
	}
}
