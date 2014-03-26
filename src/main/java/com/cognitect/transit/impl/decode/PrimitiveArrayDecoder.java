package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.util.Iterator;
import java.util.List;

public class PrimitiveArrayDecoder implements Decoder {

    public static final int INTS = 0;
    public static final int LONGS = 1;
    public static final int FLOATS = 2;
    public static final int DOUBLES = 3;
    public static final int BOOLS = 4;
    public static final int SHORTS = 5;
    public static final int CHARS = 6;

    private final int tag;

    public PrimitiveArrayDecoder(int tag) {
        this.tag = tag;
    }

    private int[] intArray(List<Long> l) {
        int[] a = new int[l.size()];
        Iterator<Long> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = iter.next().intValue();
        return a;
    }

    private long[] longArray(List<Long> l) {
        long[] a = new long[l.size()];
        Iterator<Long> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = iter.next().longValue();
        return a;
    }

    private float[] floatArray(List<Double> l) {
        float[] a = new float[l.size()];
        Iterator<Double> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = iter.next().floatValue();
        return a;
    }

    private boolean[] boolArray(List<Boolean> l) {
        boolean[] a = new boolean[l.size()];
        Iterator<Boolean> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = iter.next().booleanValue();
        return a;
    }

    private double[] doubleArray(List<Double> l) {
        double[] a = new double[l.size()];
        Iterator<Double> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = iter.next().doubleValue();
        return a;
    }

    private short[] shortArray(List<Long> l) {
        short[] a = new short[l.size()];
        Iterator<Long> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = iter.next().shortValue();
        return a;
    }

    private char[] charArray(List<Long> l) {
        char[] a = new char[l.size()];
        Iterator<Long> iter = l.iterator();
        int i = 0;
        while(iter.hasNext())
            a[i++] = (char)iter.next().intValue();
        return a;
    }

    @Override
    public Object decode(Object encodedVal) {

        Object ret = null;
        switch(tag) {
            case INTS: ret = intArray((List<Long>)encodedVal); break;
            case LONGS: ret = longArray((List<Long>)encodedVal); break;
            case FLOATS: ret = floatArray((List<Double>)encodedVal); break;
            case BOOLS: ret = boolArray((List<Boolean>)encodedVal); break;
            case DOUBLES: ret = doubleArray((List<Double>)encodedVal); break;
            case SHORTS: ret = shortArray((List<Long>)encodedVal); break;
            case CHARS: ret = charArray((List<Long>)encodedVal); break;
        }

        return ret;
    }
}
