using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Protoserial
{
    public class Manager
    {
        public void RegisterMessageType ( System.Type type )
        {
            var attrs = type.GetCustomAttributes(true).OfType<Message>();
            if ( attrs.Any() )
            {
                System.Console.WriteLine("It's a valid object");
            }
            System.Console.WriteLine(type.Name);
        }
    }
}
