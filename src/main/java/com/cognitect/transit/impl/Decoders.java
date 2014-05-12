// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.Keyword;
import com.cognitect.transit.Ratio;
import com.cognitect.transit.Symbol;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Decoders{
public static class BigDecimalDecoder implements Decoder{

    @Override
    public Object decode(Object encodedVal) {
        return new BigDecimal((String)encodedVal);
    }
}

public static class BinaryDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return Base64.decodeBase64(((String) encodedVal).getBytes());
    }
}

public static class BooleanDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return encodedVal.equals("t");
    }
}

public static class CharacterDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return ((String)encodedVal).charAt(0);
    }
}

public static class CmapDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List array = (List)encodedVal;
        Map m = new HashMap(array.size()/2);

        for(int i=0;i<array.size();i+=2) {
            m.put(array.get(i), array.get(i+1));
        }

        return m;
    }
}

public static class DoubleDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return new Double((String)encodedVal);
    }
}

public static class IdentityDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return encodedVal;
    }
}

public static class IntegerDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        String enc = (String)encodedVal;
        try {
            return Long.parseLong(enc);
        }catch(NumberFormatException e) {
            return new BigInteger(enc);
        }
    }
}

public static class KeywordDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return new Keyword((String)encodedVal);
    }
}

public static class ListDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List list = new LinkedList();
        List array = (List)encodedVal;

        Iterator i = array.iterator();
        while(i.hasNext()) {
            list.add(i.next());
        }

        return list;
    }
}

public static class NullDecoder implements Decoder {

    @Override
    public Object decode(Object ignored) {
        return null;
    }
}

public static class PrimitiveArrayDecoder implements Decoder {

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

public static class RatioDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List array = (List)encodedVal;

        return new Ratio((Long)array.get(0), (Long)array.get(1));

    }
}

public static class SetDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List list = (List)encodedVal;
	    Set set = new HashSet(list.size());

        Iterator i = list.iterator();
        while(i.hasNext()) {
            set.add(i.next());
        }

        return set;
    }
}

public static class SymbolDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return new Symbol((String)encodedVal);
    }
}

public static class TimeDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        if(encodedVal instanceof String) {
            try {
                return javax.xml.bind.DatatypeConverter.parseDateTime((String)encodedVal).getTime();
            } catch(Exception e) {
                // TODO: What should happen here?
                System.out.println("WARNING: Could not decode time: " + encodedVal);
                return Constants.ESC_STR + "t" + encodedVal;
            }
        }
        else {
            return new Date((Long)encodedVal);
        }

    }
}

public static class URIDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        try {
            return new URI((String)encodedVal);
        } catch (URISyntaxException e) {
            // TODO: What should happen here
            System.out.println("WARNING: Could not decode URI: " + encodedVal);
            return Constants.ESC + "r" + encodedVal;
        }
    }
}

public static class UUIDDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        if(encodedVal instanceof String) {
            return UUID.fromString((String)encodedVal);
        }
        else {
            List<Long> l = (List<Long>)encodedVal;
            return new UUID(l.get(0), l.get(1));
        }
    }
}
}
