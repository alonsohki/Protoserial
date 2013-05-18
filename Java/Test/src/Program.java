import com.conditionracer.protoserial.*;

public class Program
{
	public static void main ( String[] args )
	{
		Manager manager = new Manager ();
		
		manager.RegisterMessageType(MyMessage.class);
		manager.RegisterMessageType(OtherMessage.class);
		manager.RegisterMessageType(ParentMessage.class);
		
		System.out.println("Hello world");
	}
}
