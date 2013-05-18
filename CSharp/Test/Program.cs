using System;
using System.IO;
using Protoserial;

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
            msg.varIntegerNullable = 12;
            msg.varInteger = 9001;
            msg.varString = "Hello world";
            msg.varFloat = 1.0f;
            msg.varLong = 99999999999999;
            msg.varUint = 200;
            msg.varOther.varXXX = 100;
            msg.requiredField = "";

            var into = new FileStream("output.txt", FileMode.Create);
            manager.Serialize(msg, into);
            into.Close();

            var from = new FileStream("output.txt", FileMode.Open);
            msg = (MyMessage)manager.Deserialize(from);
            from.Close();

            Console.WriteLine("Press any key to close...");
            Console.ReadLine();
        }
    }
}
