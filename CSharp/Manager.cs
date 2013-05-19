using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Protoserial.Methods;

namespace Protoserial
{
    struct NameHash
    {
        public short OriginalHash;
        public short ActualHash;
        public string Name;
    }

    class FieldData
    {
        public NameHash Hash;
        public FieldInfo Info;
        public bool Required;
    }

    class DictionaryEntry
    {
        public NameHash Hash;
        public Type Type;
        public List<FieldData> Fields;
    }

    public class Manager
    {
        private readonly Dictionary<Type, DictionaryEntry> mTypes = new Dictionary<Type, DictionaryEntry>();
        private readonly Dictionary<Type, ISerializationMethod> mMethods = new Dictionary<Type, ISerializationMethod>();
        private readonly ISerializationMethod[] mMethodsByID = new ISerializationMethod[256];
        private readonly ISerializationMethod mInnerSerializer;

        public Manager()
        {
            for (int i = 0; i < mMethodsByID.Length; ++i)
                mMethodsByID[i] = null;

            // Register the inner object serializer (special case)
            mInnerSerializer = new SerializeInnerObject(this);
            mMethodsByID[mInnerSerializer.GetMethodID()] = mInnerSerializer;

            AddMethod(typeof (Int16), new Methods.SerializeInt16());
            AddMethod(typeof (UInt16), new Methods.SerializeUInt16());
            AddMethod(typeof (Int32), new Methods.SerializeInt32());
            AddMethod(typeof (UInt32), new Methods.SerializeUInt32());
            AddMethod(typeof (Int64), new Methods.SerializeInt64());
            AddMethod(typeof (UInt64), new Methods.SerializeUInt64());
            AddMethod(typeof (float), new Methods.SerializeFloat());
            AddMethod(typeof (double), new Methods.SerializeDouble());
            AddMethod(typeof (string), new Methods.SerializeString());
        }

        public void AddMethod(Type type, ISerializationMethod method)
        {
            if ( mMethodsByID[method.GetMethodID()] != null )
                throw new SerializationMethodCollisionException(type.Name);
            mMethodsByID[method.GetMethodID()] = method;
            mMethods.Add(type, method);
        }

        public void RegisterMessageType(Type type)
        {
            var attrs = type.GetCustomAttributes(true).OfType<Message>();
            if (attrs.Any())
            {
                // Get the hash from the type name, and check for collisions
                short hash = Crc16.Calc(type.Name);
                bool collided = false;
                foreach (var item in mTypes.Keys)
                {
                    var value = mTypes[item];
                    if (value.Hash.OriginalHash == hash)
                    {
                        collided = true;
                        value.Hash.ActualHash = 0;
                        break;
                    }
                }

                var entry = new DictionaryEntry
                {
                    Type = type,
                    Hash =
                        {
                            OriginalHash = hash,
                            ActualHash = collided ? (short) 0 : hash,
                            Name = type.Name
                        }
                };

                // Find all the type members and add them
                var fields = type.GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.FlattenHierarchy);
                LoadFields(fields, out entry.Fields);

                mTypes.Add(type, entry);
            }
            else
            {
                throw new Protoserial.NotAMessageException(type.Name);
            }
        }

        public void Serialize(object obj, Stream into)
        {
            Type type = obj.GetType();
            if (mTypes.ContainsKey(type) == false)
                throw new Protoserial.UnknownTypeException(type.Name);

            var entry = mTypes[type];

            // Write the object type name
            WriteHash(entry.Hash, into);

            // Write all the fields
            foreach (var field in entry.Fields)
            {
                var value = field.Info.GetValue(obj);
                if (value == null)
                {
                    if (field.Required)
                        throw new RequiredFieldException(field.Info.Name);
                }
                else
                {
                    type = field.Info.FieldType;

                    // Handle nullable types
                    if (type.IsGenericType && type.GetGenericTypeDefinition() == typeof (Nullable<>))
                    {
                        var enclosedType = type.GetGenericArguments()[0];
                        value = type.GetProperty("Value").GetValue(value);
                        type = enclosedType;
                    }

                    WriteTypeID(type, @into);
                    WriteHash(field.Hash, @into);
                    WriteValue(type, value, @into);
                }
            }

            // Write a byte set to zero to mark the end of the type
            into.WriteByte(0);
        }

        public object Deserialize(Stream from)
        {
            NameHash hash = ReadHash(from);
            object o = null;

            // Instantiate the object from its hash
            var entry = GetEntryForHash(hash);
            if ( entry != null )
            {
                o = Activator.CreateInstance(entry.Type);

                // Read all the fields
                byte typeID;
                while ((typeID = ReadTypeID(@from)) != 0)
                {
                    hash = ReadHash(from);

                    FieldData data = GetFieldForHash(hash, entry);
                    if (data == null)
                    {
                        // Request for a field that we don't know. This probably means that the peer
                        // is using a newer version of the message and has added new fields. Skip the
                        // type bytes.
                        if ( mMethodsByID[typeID] == null )
                            throw new UnknownTypeException("");
                        mMethodsByID[typeID].Read(@from);
                    }
                    if (data != null)
                    {
                        var type = data.Info.FieldType;

                        // Handle nullable types
                        if (type.IsGenericType && type.GetGenericTypeDefinition() == typeof (Nullable<>))
                        {
                            var enclosedType = type.GetGenericArguments()[0];
                            var value = ReadValue(enclosedType, from);
                            if (value != null)
                            {
                                data.Info.SetValue(o, value);
                            }
                        }
                        else
                        {
                            data.Info.SetValue(o, ReadValue(type, from));
                        }
                    }
                }
            }

            return o;
        }






        // Private utility functions
        private static void LoadFields(IEnumerable<FieldInfo> fields, out List<FieldData> into)
        {
            into = new List<FieldData>();

            foreach (var field in fields)
            {
                short hash = Crc16.Calc(field.Name);

                var newField = new FieldData();
                newField.Hash.OriginalHash = hash;
                newField.Hash.Name = field.Name;

                // Check for hash collisions
                var collision = into.Find(f => f.Hash.OriginalHash == hash);
                if (collision == null)
                {
                    newField.Hash.ActualHash = hash;
                }
                else
                {
                    collision.Hash.ActualHash = 0;
                    newField.Hash.ActualHash = 0;
                }

                newField.Required = field.GetCustomAttribute<Required>() != null;
                newField.Info = field;

                into.Add(newField);
            }
        }

        private void WriteTypeID(Type type, Stream @into)
        {
            if ( mMethods.ContainsKey(type) )
            {
                into.WriteByte(mMethods[type].GetMethodID());
            }
            else if ( mTypes.ContainsKey(type) )
            {
                into.WriteByte(mInnerSerializer.GetMethodID());
            }
            else
            {
                throw new UnknownTypeException(type.Name);
            }
        }

        private byte ReadTypeID(Stream @from)
        {
            return (byte)from.ReadByte();
        }

        private void WriteHash(NameHash hash, Stream into)
        {
            bool hasAHash = hash.ActualHash != 0;

            // Write the hash value
            byte[] bytes = BitConverter.GetBytes(hash.ActualHash);
            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);
            into.Write(bytes, 0, 2);

            // Having a hash of 0 means that this entity had hash collisions, so we
            // will write the full name.
            if (hash.ActualHash == 0)
            {
                mMethods[typeof(string)].Write(hash.Name, into);
            }
        }

        private NameHash ReadHash(Stream from)
        {
            NameHash hash = new NameHash();

            // Read the hash value
            byte[] bytes = new byte[2];
            from.Read(bytes, 0, 2);
            if (!BitConverter.IsLittleEndian)
                Array.Reverse(bytes);
            hash.ActualHash = BitConverter.ToInt16(bytes, 0);

            if (hash.ActualHash == 0)
            {
                hash.Name = (string)mMethods[typeof(string)].Read(@from);
            }
            return hash;
        }

        private void WriteValue(Type type, object value, Stream @into)
        {
            if (mMethods.ContainsKey(type))
            {
                mMethods[type].Write(value, @into);
            }
            else if (mTypes.ContainsKey(type))
            {
                mInnerSerializer.Write(value, @into);
            }
            else
            {
                throw new UnknownTypeException(type.Name);
            }
        }

        private object ReadValue(Type type, Stream @from)
        {
            if (mMethods.ContainsKey(type))
            {
                return mMethods[type].Read(@from);
            }
            else if (mTypes.ContainsKey(type))
            {
                return mInnerSerializer.Read(@from);
            }
            else
            {
                throw new UnknownTypeException(type.Name);
            }
        }

        private DictionaryEntry GetEntryForHash(NameHash hash)
        {
            if (hash.ActualHash == 0)
            {
                if (hash.Name.Length == 0)
                    throw new UnknownTypeException("");

                foreach (var cur in mTypes)
                {
                    if (cur.Value.Hash.Name.Equals(hash.Name))
                    {
                        return cur.Value;
                    }
                }
            }
            else
            {
                foreach (var cur in mTypes)
                {
                    if (cur.Value.Hash.ActualHash == hash.ActualHash)
                    {
                        return cur.Value;
                    }
                }
            }

            return null;
        }

        private FieldData GetFieldForHash(NameHash hash, DictionaryEntry entry )
        {
            if ( hash.ActualHash == 0 )
            {
                if (hash.Name.Length == 0)
                    throw new UnknownTypeException("");

                foreach ( var field in entry.Fields )
                {
                    if (field.Hash.Name.Equals(hash.Name))
                        return field;
                }
            }
            else
            {
                foreach ( var field in entry.Fields )
                {
                    if (field.Hash.ActualHash == hash.ActualHash)
                        return field;
                }
            }

            return null;
        }

    }
}
