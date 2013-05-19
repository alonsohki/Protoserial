package com.conditionracer.protoserial;

public class NotAMessageException extends Exception
{
	private static final long serialVersionUID = -5936401922491811260L;

	public NotAMessageException ( String name )
	{
		super ( name );
	}
}
