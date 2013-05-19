package com.conditionracer.protoserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerializeInnerObject implements ISerializationMethod
{
	private Manager mManager;
	
	public SerializeInnerObject ( Manager manager )
	{
		mManager = manager;
	}
	
	@Override public int GetMethodID () { return 19; }
	
	@Override
	public Object Read ( InputStream from ) throws InstantiationException, IllegalAccessException, IOException, UnknownTypeException
	{
		return mManager.Deserialize(from);
	}
	
	@Override
	public void Write ( Object obj, OutputStream into ) throws IOException, UnknownTypeException, RequiredFieldException
	{
		mManager.Serialize(obj, into);
	}
}
