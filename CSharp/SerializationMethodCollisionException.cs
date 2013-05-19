using System;

namespace Protoserial
{
    class SerializationMethodCollisionException : Exception
    {
        public string Name;

        public SerializationMethodCollisionException ( string name )
        {
            Name = name;
        }
    }
}
