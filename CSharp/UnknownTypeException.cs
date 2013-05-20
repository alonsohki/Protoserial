using System;

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
