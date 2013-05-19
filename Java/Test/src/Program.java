import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.conditionracer.protoserial.*;

public class Program
{
	public static void main ( String[] args )
	{
		Manager manager = new Manager ();
		
		manager.RegisterMessageType(MyMessage.class);
		manager.RegisterMessageType(OtherMessage.class);
		manager.RegisterMessageType(ParentMessage.class);
		
		try {
			FileInputStream from = new FileInputStream ( "output.txt" );
			@SuppressWarnings("unused")
			MyMessage msg = (MyMessage) manager.Deserialize ( from );
			from.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
