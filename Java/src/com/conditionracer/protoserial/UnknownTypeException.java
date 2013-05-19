package com.conditionracer.protoserial;

public class UnknownTypeException extends Exception
{
	private static final long serialVersionUID = 7115674238166865708L;

	public UnknownTypeException ( String name )
	{
		super ( name );
	}
}
