package com.conditionracer.protoserial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISerializationMethod
{
	Object Read ( InputStream from ) throws IOException;
	void Write ( Object obj, OutputStream to ) throws IOException;
}
