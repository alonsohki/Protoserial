using System.IO;

namespace Protoserial
{
    public interface ISerializationMethod
    {
        object Read (Stream from);
        void Write (object o, Stream into);
    }
}
