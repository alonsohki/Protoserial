using System;

namespace Protoserial
{
    class RequiredFieldException : Exception
    {
        public string Name;

        public RequiredFieldException ( string name )
        {
            Name = name;
        }
    }
}
