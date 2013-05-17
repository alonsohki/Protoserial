using Protoserial;

namespace Test
{
    [Protoserial.Message]
    struct MyMessage
    {
        public int? varInteger;
        public string varString;
    }
}
