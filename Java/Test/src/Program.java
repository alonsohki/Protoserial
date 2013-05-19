import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.conditionracer.protoserial.*;

public class Program
{
	public static void main ( String[] args )
	{
		try {
			Manager manager = new Manager ();
			
			manager.RegisterMessageType(MyMessage.class);
			manager.RegisterMessageType(OtherMessage.class);
			manager.RegisterMessageType(ParentMessage.class);
			
            MyMessage msg = new MyMessage();
            msg.id = -1L;
            msg.varIntegerNullable = null;
            msg.varUshort = 1;
            msg.varShort = -1;
            msg.varUint = 9001;
            msg.varInteger = -9001;
            msg.varUlong = 9999999999999L;
            msg.varLong = -9999999999999L;
            msg.varFloat = 1.0f;
            msg.varDouble = 9421844.442;
            msg.varString = "Hello world";
            msg.varOther = new OtherMessage();
            msg.varOther.varXXX = 100L;
            msg.requiredField = "";
            msg.varRepeatedInt = new Integer[] { 1, 2, 3, 4, 5 };
            msg.varRepeatedOther = new OtherMessage[] { new OtherMessage(), new OtherMessage() };
            msg.varRepeatedOther[0].varXXX = 1000L;
            msg.varRepeatedOther[1].varXXX = 2000L;
            
			FileOutputStream into = new FileOutputStream ( "output.txt" );
			manager.Serialize ( msg, into );
			into.close();
			
			FileInputStream from = new FileInputStream ( "output.txt" );
			msg = (MyMessage) manager.Deserialize ( from );
			from.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
