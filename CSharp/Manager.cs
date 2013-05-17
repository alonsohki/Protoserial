using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace Protoserial
{
    struct NameHash
    {
        public ushort OriginalHash;
        public ushort ActualHash;
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
        public List<FieldData> Fields;
    }

    public class Manager
    {
        private readonly Dictionary<Type, DictionaryEntry> mTypes = new Dictionary<Type, DictionaryEntry>();

        private void LoadFields ( IEnumerable<FieldInfo> fields, out List<FieldData> into )
        {
            into = new List<FieldData>();

            foreach (var field in fields)
            {
                ushort hash = Crc16.Calc(field.Name);

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

                bool required = true;
                if (field.FieldType.GetCustomAttribute<Required>() == null)
                {
                    if ( field.FieldType.IsGenericType && field.FieldType.GetGenericTypeDefinition() == typeof(Nullable<>) )
                    {
                        required = false;
                    }
                }
                newField.Required = required;
                newField.Info = field;

                into.Add(newField);
            }
        }
  
        public void RegisterMessageType ( Type type )
        {
            var attrs = type.GetCustomAttributes(true).OfType<Message>();
            if ( attrs.Any() )
            {
                // Get the hash from the type name, and check for collisions
                ushort hash = Crc16.Calc(type.Name);
                bool collided = false;
                foreach ( var item in mTypes.Keys )
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
                    Hash =
                    {
                        OriginalHash = hash,
                        ActualHash = collided ? (ushort) 0 : hash,
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

        public void Serialize ( object obj, Stream into )
        {
            Type type = obj.GetType();
            if (mTypes.ContainsKey(type) == false)
                throw new Protoserial.UnknownTypeException(type.Name);

            var entry = mTypes[type];

            // Write the object type name

        }
    }
}
