
using System;
using System.IO;

namespace Protoserial.Methods
{
    class SerializeInt32 : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadInt32();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((Int32)o);
        }
    }

    class SerializeUInt32 : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadUInt32();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((UInt32)o);
        }
    }

    class SerializeInt64 : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadInt64();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((Int64)o);
        }
    }

    class SerializeUInt64 : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadInt64();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((Int64)o);
        }
    }

    class SerializeFloat : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadSingle();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((float)o);
        }
    }

    class SerializeDouble : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadDouble();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((double)o);
        }
    }

    class SerializeString : ISerializationMethod
    {
        public object Read(Stream @from)
        {
            var reader = new BinaryReader(@from);
            return reader.ReadString();
        }

        public void Write(object o, Stream @into)
        {
            var writer = new BinaryWriter(@into);
            writer.Write((string)o);
        }
    }
}
