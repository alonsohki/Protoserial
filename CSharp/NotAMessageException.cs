using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Protoserial
{
    class NotAMessageException : Exception
    {
        public string Name;

        public NotAMessageException ( string name )
        {
            Name = name;
        }
    }
}
