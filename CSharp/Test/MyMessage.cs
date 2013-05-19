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
        public ulong varUlong;
        public long varLong;
        public float varFloat;
        public double varDouble;
        public string varString;
        public OtherMessage varOther;

        [Required] public string requiredField;
    }
}
