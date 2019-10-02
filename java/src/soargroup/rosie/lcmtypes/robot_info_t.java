/* LCM type definition class file
 * This file was automatically generated by lcm-gen
 * DO NOT MODIFY BY HAND!!!!
 */

package soargroup.rosie.lcmtypes;
 
import java.io.*;
import java.util.*;
import lcm.lcm.*;
 
public final class robot_info_t implements lcm.lcm.LCMEncodable
{
    public long utime;
    public double xyzrpy[];
    public int held_object;
 
    public robot_info_t()
    {
        xyzrpy = new double[6];
    }
 
    public static final long LCM_FINGERPRINT;
    public static final long LCM_FINGERPRINT_BASE = 0xe32ab652c1e6c003L;
 
    static {
        LCM_FINGERPRINT = _hashRecursive(new ArrayList<Class<?>>());
    }
 
    public static long _hashRecursive(ArrayList<Class<?>> classes)
    {
        if (classes.contains(soargroup.rosie.lcmtypes.robot_info_t.class))
            return 0L;
 
        classes.add(soargroup.rosie.lcmtypes.robot_info_t.class);
        long hash = LCM_FINGERPRINT_BASE
            ;
        classes.remove(classes.size() - 1);
        return (hash<<1) + ((hash>>63)&1);
    }
 
    public void encode(DataOutput outs) throws IOException
    {
        outs.writeLong(LCM_FINGERPRINT);
        _encodeRecursive(outs);
    }
 
    public void _encodeRecursive(DataOutput outs) throws IOException
    {
        outs.writeLong(this.utime); 
 
        for (int a = 0; a < 6; a++) {
            outs.writeDouble(this.xyzrpy[a]); 
        }
 
        outs.writeInt(this.held_object); 
 
    }
 
    public robot_info_t(byte[] data) throws IOException
    {
        this(new LCMDataInputStream(data));
    }
 
    public robot_info_t(DataInput ins) throws IOException
    {
        if (ins.readLong() != LCM_FINGERPRINT)
            throw new IOException("LCM Decode error: bad fingerprint");
 
        _decodeRecursive(ins);
    }
 
    public static soargroup.rosie.lcmtypes.robot_info_t _decodeRecursiveFactory(DataInput ins) throws IOException
    {
        soargroup.rosie.lcmtypes.robot_info_t o = new soargroup.rosie.lcmtypes.robot_info_t();
        o._decodeRecursive(ins);
        return o;
    }
 
    public void _decodeRecursive(DataInput ins) throws IOException
    {
        this.utime = ins.readLong();
 
        this.xyzrpy = new double[(int) 6];
        for (int a = 0; a < 6; a++) {
            this.xyzrpy[a] = ins.readDouble();
        }
 
        this.held_object = ins.readInt();
 
    }
 
    public soargroup.rosie.lcmtypes.robot_info_t copy()
    {
        soargroup.rosie.lcmtypes.robot_info_t outobj = new soargroup.rosie.lcmtypes.robot_info_t();
        outobj.utime = this.utime;
 
        outobj.xyzrpy = new double[(int) 6];
        System.arraycopy(this.xyzrpy, 0, outobj.xyzrpy, 0, 6); 
        outobj.held_object = this.held_object;
 
        return outobj;
    }
 
}
