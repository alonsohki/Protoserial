using System;
using System.IO;

namespace Test
{
    class Program
    {
        static void Main(string[] args)
        {
            Protoserial.Manager manager = new Protoserial.Manager();
            manager.RegisterMessageType(typeof(MyMessage));
            manager.RegisterMessageType(typeof(ParentMessage));
            manager.RegisterMessageType(typeof(OtherMessage));

            MyMessage msg = new MyMessage();
            msg.id = -1;
            msg.varIntegerNullable = null;
            msg.varUshort = 1;
            msg.varShort = -1;
            msg.varUint = 9001;
            msg.varInteger = -9001;
            msg.varUlong = 9999999999999;
            msg.varLong = -9999999999999;
            msg.varFloat = 1.0f;
            msg.varDouble = 9421844.442;
            msg.varString = "Hello world";
            msg.varOther = new OtherMessage();
            msg.varOther.varXXX = 100;
            msg.requiredField = "";

            var into = new FileStream("output.txt", FileMode.Create);
            manager.Serialize(msg, @into);
            into.Close();

            var from = new FileStream("output.txt", FileMode.Open);
            msg = (MyMessage)manager.Deserialize(from);
            from.Close();

            Console.WriteLine("Press any key to close...");
            Console.ReadLine();
        }
    }
}
