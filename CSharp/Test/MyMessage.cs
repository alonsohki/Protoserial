using Protoserial;

namespace Test
{
    [Protoserial.Message]
    class MyMessage : ParentMessage
    {
        public int? varIntegerNullable;
        public ushort varUshort;
        public short varShort;
        public uint varUint;
        public int varInteger;
        public long varLong;
        public string varString;
        public float varFloat;
        public OtherMessage varOther;

        [Required] public string requiredField;
    }
}
