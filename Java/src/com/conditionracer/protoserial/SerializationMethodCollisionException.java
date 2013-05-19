package com.conditionracer.protoserial;

public class SerializationMethodCollisionException extends Exception
{
	private static final long serialVersionUID = 8284666232681707634L;

	public SerializationMethodCollisionException ( String name )
	{
		super ( name );
	}
}
