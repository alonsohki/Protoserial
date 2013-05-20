using System;

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
