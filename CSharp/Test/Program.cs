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

            MyMessage msg;
            msg.varInteger = null;
            msg.varString = "Hello world";

            var into = new FileStream("output.txt", FileMode.Create);
            manager.Serialize(msg, into);
            into.Close();

            Console.WriteLine("Press any key to close...");
            Console.ReadLine();
        }
    }
}
