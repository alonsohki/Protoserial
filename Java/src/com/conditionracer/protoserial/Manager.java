package com.conditionracer.protoserial;

public class Manager
{
	private class NameHash
	{
		String name;
		short originalHash;
		short actualHash;
	}
	
	@SuppressWarnings("rawtypes")
	public void RegisterMessageType ( Class type )
	{
		short hash = Crc16.Calc(type.getName());
	}
}
