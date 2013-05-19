
using System;
using System.IO;

namespace Protoserial.Methods
{
    class SerializeInt16 : ISerializationMethod
    {
        public byte GetMethodID() { return 1; }

        public object Read(Stream @from)
        {
            byte[] bytes = new byte[2];
            from.Read(bytes, 0, 2);

            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);

            return BitConverter.ToInt16(bytes, 0);
        }

        public void Write(object o, Stream @into)
        {
            byte[] bytes = BitConverter.GetBytes((Int16)o);

            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);

            into.Write(bytes, 0, 2);
        }
    }

    class SerializeUInt16 : ISerializationMethod
    {
        public byte GetMethodID() { return 2; }

        public object Read(Stream @from)
        {
            return VarIntSerializer.ReadUInt16(@from);
        }

        public void Write(object o, Stream @into)
        {
            VarIntSerializer.WriteUInt16((UInt16)o, @into);
        }
    }

    class SerializeInt32 : ISerializationMethod
    {
        public byte GetMethodID() { return 3; }

        public object Read(Stream @from)
        {
            byte[] bytes = new byte[4];
            from.Read(bytes, 0, 4);

            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);

            return BitConverter.ToInt32(bytes, 0);
        }

        public void Write(object o, Stream @into)
        {
            byte[] bytes = BitConverter.GetBytes((Int32)o);

            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);

            into.Write(bytes, 0, 4);
        }
    }

    class SerializeUInt32 : ISerializationMethod
    {
        public byte GetMethodID() { return 4; }

        public object Read(Stream @from)
        {
            return VarIntSerializer.ReadUInt32(@from);
        }

        public void Write(object o, Stream @into)
        {
            VarIntSerializer.WriteUInt32((UInt32)o, @into);
        }
    }

    class SerializeInt64 : ISerializationMethod
    {
        public byte GetMethodID() { return 5; }

        public object Read(Stream @from)
        {
            byte[] bytes = new byte[8];
            from.Read(bytes, 0, 8);

            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);

            return BitConverter.ToInt64(bytes, 0);
        }

        public void Write(object o, Stream @into)
        {
            byte[] bytes = BitConverter.GetBytes((Int64)o);

            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);

            into.Write(bytes, 0, 8);
        }
    }

    class SerializeUInt64 : ISerializationMethod
    {
        public byte GetMethodID() { return 6; }

        public object Read(Stream @from)
        {
            return VarIntSerializer.ReadUInt64(@from);
        }

        public void Write(object o, Stream @into)
        {
            VarIntSerializer.WriteUInt64((UInt64)o, @into);
        }
    }

    class SerializeFloat : ISerializationMethod
    {
        public byte GetMethodID() { return 7; }

        public object Read(Stream @from)
        {
            byte[] bytes = new byte[4];
            from.Read(bytes, 0, 4);
            return BitConverter.ToSingle(bytes, 0);
        }

        public void Write(object o, Stream @into)
        {
            byte[] bytes = BitConverter.GetBytes((float)o);
            into.Write(bytes, 0, 4);
        }
    }

    class SerializeDouble : ISerializationMethod
    {
        public byte GetMethodID() { return 8; }

        public object Read(Stream @from)
        {
            byte[] bytes = new byte[8];
            from.Read(bytes, 0, 8);
            return BitConverter.ToDouble(bytes, 0);
        }

        public void Write(object o, Stream @into)
        {
            byte[] bytes = BitConverter.GetBytes((double)o);
            into.Write(bytes, 0, 8);
        }
    }

    class SerializeString : ISerializationMethod
    {
        public byte GetMethodID() { return 9; }

        public object Read(Stream @from)
        {
            UInt16 length = VarIntSerializer.ReadUInt16(@from);
            byte[] strData = new byte[length];
            from.Read(strData, 0, length);
            return System.Text.Encoding.UTF8.GetString(strData);
        }

        public void Write(object o, Stream @into)
        {
            byte[] strData = System.Text.Encoding.UTF8.GetBytes ((string)o);
            VarIntSerializer.WriteUInt16((ushort)strData.Length, @into);
            into.Write(strData, 0, strData.Length);
        }
    }

    class SerializeInnerObject : ISerializationMethod
    {
        private readonly Manager mManager;
        public SerializeInnerObject ( Manager manager )
        {
            mManager = manager;
        }

        public byte GetMethodID() { return 10; }

        public object Read(Stream @from)
        {
            return mManager.Deserialize(@from);
        }

        public void Write(object o, Stream @into)
        {
            mManager.Serialize(o, @into);
        }
    }
}
