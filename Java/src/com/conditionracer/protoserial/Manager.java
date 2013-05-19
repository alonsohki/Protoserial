package com.conditionracer.protoserial;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class Manager
{
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
	
	public Manager ()
	{
		SerializationMethods.RegisterMethods(this);
	}
	
	
	public void AddMethod ( Class type, ISerializationMethod method )
	{
		mMethods.put ( type, method );
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
	
	public Object Deserialize ( InputStream from ) throws IOException, InstantiationException, IllegalAccessException
	{
		Object o = null;
		NameHash hash = ReadHash ( from );
		DictionaryEntry entry = GetEntryFromHash ( hash );
		
		if ( entry != null )
		{
			o = entry.Type.newInstance();
			
			for ( FieldData field : entry.Fields )
			{
				
			}
		}
		
		return o;
	}
	
	
	
	// Private utility methods
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
}
