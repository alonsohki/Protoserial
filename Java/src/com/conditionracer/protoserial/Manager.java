package com.conditionracer.protoserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class Manager
{
	public enum MethodType
	{
		TYPE_SIGNED,
		TYPE_UNSIGNED
	}
	
	private class NameHash
	{
		String Name;
		short OriginalHash;
		short ActualHash;
	}
	
	private class FieldData
	{
		public NameHash Hash;
		public Field Info;
		public boolean Required;
		public boolean Unsigned;
	}
	
	private class DictionaryEntry
	{
		public NameHash Hash;
		public Class Type;
		public List<FieldData> Fields;
	}
	
	private HashMap<Class, DictionaryEntry> mTypes = new HashMap<Class, DictionaryEntry>();
	private HashMap<Class, ISerializationMethod> mMethods = new HashMap<Class, ISerializationMethod>();
	private HashMap<Class, ISerializationMethod> mUnsignedMethods = new HashMap<Class, ISerializationMethod>();
	private ISerializationMethod[] mMethodsByID = new ISerializationMethod[256];
	private ISerializationMethod mInnerSerializer;
    private static byte END_OF_FIELDS_TYPE_ID = 0;
    private static byte REPEATED_FIELD_TYPE_ID = 1;
	
	public Manager () throws SerializationMethodCollisionException
	{
        for (int i = 0; i < mMethodsByID.length; ++i)
            mMethodsByID[i] = null;
        
        // Initialize the special inner object serializer
		mInnerSerializer = new SerializeInnerObject(this);
		mMethodsByID[mInnerSerializer.GetMethodID()] = mInnerSerializer;
		
		SerializationMethods.RegisterMethods(this);
	}
	
	
	public void AddMethod ( Class type, MethodType methodType, ISerializationMethod method ) throws SerializationMethodCollisionException
	{
		if ( mMethodsByID[method.GetMethodID()] != null )
			throw new SerializationMethodCollisionException(type.getName());
		mMethodsByID[method.GetMethodID()] = method;
		
		if ( methodType == MethodType.TYPE_SIGNED )
			mMethods.put ( type, method );
		else if ( methodType == MethodType.TYPE_UNSIGNED )
			mUnsignedMethods.put( type,  method );
	}

	public void RegisterMessageType ( Class type )
	{
		short hash = Crc16.Calc(type.getName());
		
		// Check for collisions
		boolean collided = false;
		for ( Entry<Class, DictionaryEntry> entry : mTypes.entrySet() )
		{
			if ( entry.getValue().Hash.ActualHash == hash )
			{
				collided = true;
				entry.getValue().Hash.ActualHash = 0;
				break;
			}
		}
		
		DictionaryEntry entry = new DictionaryEntry ();
		
		mTypes.put ( type, entry );
		
		entry.Type = type;
		entry.Hash = new NameHash ();
		entry.Hash.ActualHash = collided ? 0 : hash;
		entry.Hash.OriginalHash = hash;
		entry.Hash.Name = type.getName();
		
		// Get the type fields
		entry.Fields = new ArrayList<FieldData>();
		do
		{
			LoadFields ( type.getFields(), entry.Fields );
			type = type.getSuperclass();
		} while ( type != null );
	}
	
	public void Serialize ( Object obj, OutputStream into ) throws IOException, UnknownTypeException, RequiredFieldException
	{
		Class type = obj.getClass();
		if ( mTypes.containsKey(type) == false )
			throw new UnknownTypeException ( type.getName() );
		
		DictionaryEntry entry = mTypes.get(type);
		
		// Write the object type name
		WriteHash ( entry.Hash, into );
		
		// Write fields
		for ( FieldData field : entry.Fields )
		{
			into.flush();
			
			Object value = null;
			try {
				value = field.Info.get(obj);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				System.err.println("Error trying to get value from field " + field.Info.getName());
				e.printStackTrace();
			}
			
			if ( value == null )
			{
				if ( field.Required )
					throw new RequiredFieldException(field.Info.getName());
			}
			else
			{
				type = field.Info.getType();
				
				// Write repeated fields as [REPEATED_FIELD_TYPE_ID][Hash][length][entry type][entries...]
				if ( type.isArray() )
				{
                    into.write(REPEATED_FIELD_TYPE_ID);
                    WriteHash(field.Hash, into);

                    // Write the array count
                    int length = Array.getLength(value);
                    VarIntSerializer.WriteUInt32(length, into);
                    
                    // Write the array inner object type id
                    Class arrayEntryType = type.getComponentType();
                    WriteTypeID(arrayEntryType, into);
                    
                    // Write the array entries
                    for ( int i = 0; i < length; ++i )
                    {
                    	WriteValue ( arrayEntryType, Array.get(value, i), into, field.Unsigned);
                    }
				}
				else
				{
					WriteTypeID(type, into);
					WriteHash(field.Hash, into);
					WriteValue(type, value, into, field.Unsigned);
				}
			}
		}
		
        // Write a byte set to zero to mark the end of the type
        into.write(END_OF_FIELDS_TYPE_ID);
	}
	
	public Object Deserialize ( InputStream from ) throws IOException, InstantiationException, IllegalAccessException, UnknownTypeException
	{
		Object o = null;
		NameHash hash = ReadHash ( from );
		DictionaryEntry entry = GetEntryFromHash ( hash );
		
		if ( entry != null )
		{
			o = entry.Type.newInstance();
			
			byte typeID;
			while ( (typeID = ReadTypeID(from)) != END_OF_FIELDS_TYPE_ID && typeID != -1 )
			{
				hash = ReadHash ( from );
				FieldData data = GetFieldFromHash ( hash, entry.Fields );
				if ( data == null )
				{
                    // Request for a field that we don't know. This probably means that the peer
                    // is using a newer version of the message and has added new fields. Skip the
                    // type bytes.
					
					int count = 1;
					if ( typeID == REPEATED_FIELD_TYPE_ID )
					{
						count = VarIntSerializer.ReadUInt32(from);
						typeID = ReadTypeID(from);
					}
					
					if ( mMethodsByID[typeID] == null )
						throw new UnknownTypeException("");
					
					while ( count > 0 )
					{
						mMethodsByID[typeID].Read(from);
						--count;
					}
				}
				else
				{
					// Check for repeated fields
					if ( typeID == REPEATED_FIELD_TYPE_ID )
					{
						int count = VarIntSerializer.ReadUInt32(from);
						typeID = ReadTypeID(from);

						Class componentType = data.Info.getType().getComponentType();
						Object array = Array.newInstance(componentType, count);
						for ( int i = 0; i < count; ++i )
						{
							Object value = ReadValue ( componentType, from, data.Unsigned );
							Array.set(array, i, value);
						}
						data.Info.set(o, array);
					}
					else
					{
						Object value = ReadValue ( data.Info.getType(), from, data.Unsigned );
						data.Info.set(o, value);
					}
				}
			}
		}
		
		return o;
	}
	
	
	
	// Private utility methods
	@SuppressWarnings("resource")
	private NameHash ReadHash ( InputStream from ) throws IOException
	{
		BigEndianDataInputStream reader = new BigEndianDataInputStream ( from );
		NameHash hash = new NameHash ();
		hash.ActualHash = reader.readShort();
		if ( hash.ActualHash == 0 )
		{
			hash.Name = reader.readUTF();
		}
		return hash;
	}
	
	private void WriteHash ( NameHash hash, OutputStream into ) throws IOException
	{
		BigEndianDataOutputStream writer = new BigEndianDataOutputStream(into);
		writer.writeShort(hash.ActualHash);
		if ( hash.ActualHash == 0 )
		{
			writer.writeUTF(hash.Name);
		}
	}
	
	private byte ReadTypeID ( InputStream from ) throws IOException
	{
		return (byte)from.read();
	}
	
	private void WriteTypeID ( Class type, OutputStream into ) throws IOException, UnknownTypeException
	{
        if ( mMethods.containsKey(type) )
        {
            into.write(mMethods.get(type).GetMethodID());
        }
        else if ( mUnsignedMethods.containsKey(type) )
        {
        	into.write(mUnsignedMethods.get(type).GetMethodID());
        }
        else if ( mTypes.containsKey(type) )
        {
            into.write(mInnerSerializer.GetMethodID());
        }
        else
        {
            throw new UnknownTypeException(type.getName());
        }
	}
	
	private DictionaryEntry GetEntryFromHash ( NameHash hash )
	{
		if ( hash.ActualHash == 0 )
		{
			for ( Entry<Class, DictionaryEntry> entry : mTypes.entrySet() )
			{
				if ( entry.getValue().Hash.Name.compareTo(hash.Name) == 0 )
					return entry.getValue();
			}
		}
		else
		{
			for ( Entry<Class, DictionaryEntry> entry : mTypes.entrySet() )
			{
				if ( entry.getValue().Hash.OriginalHash == hash.ActualHash )
					return entry.getValue();
			}
		}
		return null;
	}
	
	private FieldData GetFieldFromHash ( NameHash hash, List<FieldData> fields )
	{
		if ( hash.ActualHash == 0 )
		{
			for ( FieldData field : fields )
			{
				if ( field.Hash.Name.compareTo(hash.Name) == 0 )
					return field;
			}
		}
		else
		{
			for ( FieldData field : fields )
			{
				if ( field.Hash.OriginalHash == hash.ActualHash )
					return field;
			}
		}
		
		return null;
	}
	
	private void LoadFields ( Field[] fields, List<FieldData> into )
	{
		for ( Field field : fields )
		{
			FieldData data = new FieldData ();
			data.Info = field;
			
			// Calculate the hash and check for collisions
			short hash = Crc16.Calc(field.getName());
			boolean collided = false;
			
			for ( FieldData cur : into )
			{
				if ( cur.Hash.OriginalHash == hash )
				{
					cur.Hash.ActualHash = 0;
					collided = true;
					break;
				}
			}
			
			data.Hash = new NameHash ();
			data.Hash.OriginalHash = hash;
			data.Hash.Name = field.getName();
			data.Hash.ActualHash = collided ? 0 : hash;
			
			// Check if it's required and unsigned
			data.Required = field.getAnnotation(Required.class) != null;
			data.Unsigned = field.getAnnotation(Unsigned.class) != null;
			
			into.add(data);
		}
	}
	
	private Object ReadValue ( Class type, InputStream from, boolean unsigned ) throws InstantiationException, IllegalAccessException, IOException, UnknownTypeException
	{
		if ( !unsigned && mMethods.containsKey(type) )
		{
			return mMethods.get(type).Read(from);
		}
		else if ( unsigned && mUnsignedMethods.containsKey(type) )
		{
			return mUnsignedMethods.get(type).Read(from);
		}
		else if ( mTypes.containsKey(type) )
		{
			return Deserialize ( from );
		}
		else
			throw new UnknownTypeException ( type.getName() );
	}
	
	private void WriteValue ( Class type, Object obj, OutputStream into, boolean unsigned) throws UnknownTypeException, IOException, RequiredFieldException
	{
		if ( !unsigned && mMethods.containsKey(type) )
		{
			mMethods.get(type).Write(obj, into);
		}
		else if ( unsigned && mUnsignedMethods.containsKey(type) )
		{
			mUnsignedMethods.get(type).Write(obj, into);
		}
		else if ( mTypes.containsKey(type) )
		{
			Serialize ( obj, into );
		}
		else
			throw new UnknownTypeException ( type.getName() );
	}
}
