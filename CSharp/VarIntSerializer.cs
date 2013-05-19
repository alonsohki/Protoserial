using System;
using System.IO;

namespace Protoserial
{
    class VarIntSerializer
    {
        public static void WriteUInt16(UInt16 value, Stream @to)
        {
            WriteUInt64(value, to);
        }

        public static void WriteUInt32(UInt32 value, Stream @to)
        {
            WriteUInt64(value, to);
        }

        public static void WriteUInt64(UInt64 value, Stream @to)
        {
            do
            {
                ulong b = (value & ~0x7FUL) != 0 ? 0x80UL : 0UL;
                b |= value & 0x7FUL;
                byte res = (byte)(b & 0xFF);
                to.WriteByte(res);

                value >>= 7;
            } while (value != 0);
        }

        public static UInt16 ReadUInt16(Stream @from)
        {
            ushort value = 0;
            ushort b;

            do
            {
                b = (ushort)from.ReadByte();
                value <<= 7;
                value |= (ushort)(b & 0x7FU);
            }
            while ((b & 0x80) != 0);

            return value;
        }

        public static UInt32 ReadUInt32(Stream @from)
        {
            uint value = 0;
            int b;
            int n = 0;

            do
            {
                b = from.ReadByte();
                value |= (uint)(b & 0x7FU) << n;
                n += 7;
            }
            while ((b & 0x80) != 0);

            return value;
        }

        public static UInt64 ReadUInt64(Stream @from)
        {
            ulong value = 0;
            int b;

            do
            {
                b = from.ReadByte();
                value <<= 7;
                value |= (ulong)((ushort)b & 0x7FU);
            }
            while (((ushort)b & 0x80) != 0);

            return value;
        }
    }
}
