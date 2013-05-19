package com.conditionracer.protoserial;

public class RequiredFieldException extends Exception
{
	private static final long serialVersionUID = -7698410146242662080L;

	public RequiredFieldException ( String name )
	{
		super ( name );
	}
}
