/* LCM type definition class file
 * This file was automatically generated by lcm-gen
 * DO NOT MODIFY BY HAND!!!!
 */

package soargroup.rosie.lcmtypes;
 
import java.io.*;
import java.util.*;
import lcm.lcm.*;
 
public final class observations_t implements lcm.lcm.LCMEncodable
{
    public long utime;
    public int click_id;
    public double eye[];
    public double lookat[];
    public double up[];
    public int nobs;
    public soargroup.rosie.lcmtypes.object_data_t observations[];
 
    public observations_t()
    {
        eye = new double[3];
        lookat = new double[3];
        up = new double[3];
    }
 
    public static final long LCM_FINGERPRINT;
    public static final long LCM_FINGERPRINT_BASE = 0x4f58b669abdbb647L;
 
    static {
        LCM_FINGERPRINT = _hashRecursive(new ArrayList<Class<?>>());
    }
 
    public static long _hashRecursive(ArrayList<Class<?>> classes)
    {
        if (classes.contains(soargroup.rosie.lcmtypes.observations_t.class))
            return 0L;
 
        classes.add(soargroup.rosie.lcmtypes.observations_t.class);
        long hash = LCM_FINGERPRINT_BASE
             + soargroup.rosie.lcmtypes.object_data_t._hashRecursive(classes)
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
 
        outs.writeInt(this.click_id); 
 
        for (int a = 0; a < 3; a++) {
            outs.writeDouble(this.eye[a]); 
        }
 
        for (int a = 0; a < 3; a++) {
            outs.writeDouble(this.lookat[a]); 
        }
 
        for (int a = 0; a < 3; a++) {
            outs.writeDouble(this.up[a]); 
        }
 
        outs.writeInt(this.nobs); 
 
        for (int a = 0; a < this.nobs; a++) {
            this.observations[a]._encodeRecursive(outs); 
        }
 
    }
 
    public observations_t(byte[] data) throws IOException
    {
        this(new LCMDataInputStream(data));
    }
 
    public observations_t(DataInput ins) throws IOException
    {
        if (ins.readLong() != LCM_FINGERPRINT)
            throw new IOException("LCM Decode error: bad fingerprint");
 
        _decodeRecursive(ins);
    }
 
    public static soargroup.rosie.lcmtypes.observations_t _decodeRecursiveFactory(DataInput ins) throws IOException
    {
        soargroup.rosie.lcmtypes.observations_t o = new soargroup.rosie.lcmtypes.observations_t();
        o._decodeRecursive(ins);
        return o;
    }
 
    public void _decodeRecursive(DataInput ins) throws IOException
    {
        this.utime = ins.readLong();
 
        this.click_id = ins.readInt();
 
        this.eye = new double[(int) 3];
        for (int a = 0; a < 3; a++) {
            this.eye[a] = ins.readDouble();
        }
 
        this.lookat = new double[(int) 3];
        for (int a = 0; a < 3; a++) {
            this.lookat[a] = ins.readDouble();
        }
 
        this.up = new double[(int) 3];
        for (int a = 0; a < 3; a++) {
            this.up[a] = ins.readDouble();
        }
 
        this.nobs = ins.readInt();
 
        this.observations = new soargroup.rosie.lcmtypes.object_data_t[(int) nobs];
        for (int a = 0; a < this.nobs; a++) {
            this.observations[a] = soargroup.rosie.lcmtypes.object_data_t._decodeRecursiveFactory(ins);
        }
 
    }
 
    public soargroup.rosie.lcmtypes.observations_t copy()
    {
        soargroup.rosie.lcmtypes.observations_t outobj = new soargroup.rosie.lcmtypes.observations_t();
        outobj.utime = this.utime;
 
        outobj.click_id = this.click_id;
 
        outobj.eye = new double[(int) 3];
        System.arraycopy(this.eye, 0, outobj.eye, 0, 3); 
        outobj.lookat = new double[(int) 3];
        System.arraycopy(this.lookat, 0, outobj.lookat, 0, 3); 
        outobj.up = new double[(int) 3];
        System.arraycopy(this.up, 0, outobj.up, 0, 3); 
        outobj.nobs = this.nobs;
 
        outobj.observations = new soargroup.rosie.lcmtypes.object_data_t[(int) nobs];
        for (int a = 0; a < this.nobs; a++) {
            outobj.observations[a] = this.observations[a].copy();
        }
 
        return outobj;
    }
 
}
