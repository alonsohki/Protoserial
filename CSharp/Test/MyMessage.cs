using Protoserial;

namespace Test
{
    [Protoserial.Message]
    class MyMessage : ParentMessage
    {
        public int? varIntegerNullable;
        public int varInteger;
        public long varLong;
        public uint varUint;
        public string varString;
        public float varFloat;
        public OtherMessage varOther;

        [Required] public string requiredField;
    }
}
