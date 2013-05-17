using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Protoserial
{
    class Crc16
    {
        public static ushort Calc ( byte[] data, uint offset, uint count )
        {
            uint temp;
            uint crc = 0;
            uint target = offset + count;

            for ( uint idx = offset; idx < target; ++idx )
            {
                temp = (ushort) (data[idx]) ^ (crc >> 8);
                temp ^= temp >> 4;
                temp ^= temp >> 2;
                temp ^= temp >> 1;
                crc = (crc << 8) ^ (temp << 15) ^ (temp << 2) ^ temp;
            }

            return (ushort)crc;
        }

        public static ushort Calc ( string str )
        {
            byte[] b2 = System.Text.Encoding.ASCII.GetBytes(str);
            return Calc(b2, 0, (uint)b2.Length);
        }
    }
}
