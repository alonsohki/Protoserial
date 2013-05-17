using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Protoserial
{
    class UnknownTypeException : Exception
    {
        public UnknownTypeException ( string name )
        {
            Name = name;
        }

        public string Name;
    }
}
