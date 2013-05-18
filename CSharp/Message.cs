using System;


namespace Protoserial
{
    [AttributeUsage(AttributeTargets.Struct|AttributeTargets.Class)]
    public class Message : System.Attribute
    {
    }
}
