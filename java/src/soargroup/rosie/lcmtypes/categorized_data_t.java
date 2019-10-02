/* LCM type definition class file
 * This file was automatically generated by lcm-gen
 * DO NOT MODIFY BY HAND!!!!
 */

package soargroup.rosie.lcmtypes;
 
import java.io.*;
import java.util.*;
import lcm.lcm.*;
 
public final class categorized_data_t implements lcm.lcm.LCMEncodable
{
    public soargroup.rosie.lcmtypes.category_t cat;
    public int len;
    public String label[];
    public double confidence[];
    public int num_features;
    public double features[];
 
    public categorized_data_t()
    {
    }
 
    public static final long LCM_FINGERPRINT;
    public static final long LCM_FINGERPRINT_BASE = 0x7ece1a5e068bbea2L;
 
    static {
        LCM_FINGERPRINT = _hashRecursive(new ArrayList<Class<?>>());
    }
 
    public static long _hashRecursive(ArrayList<Class<?>> classes)
    {
        if (classes.contains(soargroup.rosie.lcmtypes.categorized_data_t.class))
            return 0L;
 
        classes.add(soargroup.rosie.lcmtypes.categorized_data_t.class);
        long hash = LCM_FINGERPRINT_BASE
             + soargroup.rosie.lcmtypes.category_t._hashRecursive(classes)
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
        char[] __strbuf = null;
        this.cat._encodeRecursive(outs); 
 
        outs.writeInt(this.len); 
 
        for (int a = 0; a < this.len; a++) {
            __strbuf = new char[this.label[a].length()]; this.label[a].getChars(0, this.label[a].length(), __strbuf, 0); outs.writeInt(__strbuf.length+1); for (int _i = 0; _i < __strbuf.length; _i++) outs.write(__strbuf[_i]); outs.writeByte(0); 
        }
 
        for (int a = 0; a < this.len; a++) {
            outs.writeDouble(this.confidence[a]); 
        }
 
        outs.writeInt(this.num_features); 
 
        for (int a = 0; a < this.num_features; a++) {
            outs.writeDouble(this.features[a]); 
        }
 
    }
 
    public categorized_data_t(byte[] data) throws IOException
    {
        this(new LCMDataInputStream(data));
    }
 
    public categorized_data_t(DataInput ins) throws IOException
    {
        if (ins.readLong() != LCM_FINGERPRINT)
            throw new IOException("LCM Decode error: bad fingerprint");
 
        _decodeRecursive(ins);
    }
 
    public static soargroup.rosie.lcmtypes.categorized_data_t _decodeRecursiveFactory(DataInput ins) throws IOException
    {
        soargroup.rosie.lcmtypes.categorized_data_t o = new soargroup.rosie.lcmtypes.categorized_data_t();
        o._decodeRecursive(ins);
        return o;
    }
 
    public void _decodeRecursive(DataInput ins) throws IOException
    {
        char[] __strbuf = null;
        this.cat = soargroup.rosie.lcmtypes.category_t._decodeRecursiveFactory(ins);
 
        this.len = ins.readInt();
 
        this.label = new String[(int) len];
        for (int a = 0; a < this.len; a++) {
            __strbuf = new char[ins.readInt()-1]; for (int _i = 0; _i < __strbuf.length; _i++) __strbuf[_i] = (char) (ins.readByte()&0xff); ins.readByte(); this.label[a] = new String(__strbuf);
        }
 
        this.confidence = new double[(int) len];
        for (int a = 0; a < this.len; a++) {
            this.confidence[a] = ins.readDouble();
        }
 
        this.num_features = ins.readInt();
 
        this.features = new double[(int) num_features];
        for (int a = 0; a < this.num_features; a++) {
            this.features[a] = ins.readDouble();
        }
 
    }
 
    public soargroup.rosie.lcmtypes.categorized_data_t copy()
    {
        soargroup.rosie.lcmtypes.categorized_data_t outobj = new soargroup.rosie.lcmtypes.categorized_data_t();
        outobj.cat = this.cat.copy();
 
        outobj.len = this.len;
 
        outobj.label = new String[(int) len];
        if (this.len > 0)
            System.arraycopy(this.label, 0, outobj.label, 0, this.len); 
        outobj.confidence = new double[(int) len];
        if (this.len > 0)
            System.arraycopy(this.confidence, 0, outobj.confidence, 0, this.len); 
        outobj.num_features = this.num_features;
 
        outobj.features = new double[(int) num_features];
        if (this.num_features > 0)
            System.arraycopy(this.features, 0, outobj.features, 0, this.num_features); 
        return outobj;
    }
 
}
