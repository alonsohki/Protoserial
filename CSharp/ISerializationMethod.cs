using System.IO;

namespace Protoserial
{
    public interface ISerializationMethod
    {
        object Read (Stream from);
        void Write (object o, Stream into);

        // ID for this method. The same type should always have the same
        // ID, and it should never change. Also, make sure that there won't
        // be collisions. If you are implementing your own methods, it is
        // recommended to start at 50.
        byte GetMethodID ();
    }
}
