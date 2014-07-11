// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ReadHandlers {

    public static class BigDecimalReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return new BigDecimal((String) rep);
        }
    }

    public static class BinaryReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return Base64.decodeBase64(((String) rep).getBytes());
        }
    }

    public static class BooleanReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return rep.equals("t");
        }
    }

    public static class CharacterReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return ((String) rep).charAt(0);
        }
    }

    public static class CmapReadHandler implements ArrayReadHandler {

        @Override
        public Object fromRep(Object o) { return o; }

        @Override
        public ArrayReader arrayReader() {
            return new ArrayReader() {
                Map m = null;
                Object next_key = null;

                @Override
                public Object init() {
                    m = new HashMap();
                    return this;
                }

                @Override
                public Object init(int size) {
                    m = new HashMap(size);
                    return this;
                }

                @Override
                public Object add(Object ar, Object item) {
                    if (next_key != null) {
                        m.put(next_key, item);
                        next_key = null;
                    } else {
                        next_key = item;
                    }
                    return this;
                }

                @Override
                public Object complete(Object ar) {
                    return m;
                }
            };
        }
    }

    public static class DoubleReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return new Double((String) rep);
        }
    }

    public static class IdentityReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return rep;
        }
    }

    public static class IntegerReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            String enc = (String) rep;
            try {
                return Long.parseLong(enc);
            }catch(NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BigIntegerReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return new BigInteger((String) rep);
        }
    }

    public static class KeywordReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return TransitFactory.keyword((String) rep);
        }
    }

    public static class ListReadHandler implements ArrayReadHandler {

        @Override
        public Object fromRep(Object o) { return o; }

        @Override
        public ArrayReader arrayReader() {
            return new ArrayReader() {
                @Override
                public Object init() {
                    return new LinkedList();
                }

                @Override
                public Object init(int size) {
                    return init();
                }

                @Override
                public Object add(Object ab, Object item) {
                    ((List)ab).add(item);
                    return ab;
                }

                @Override
                public Object complete(Object ar) {
                    return ar;
                }
            };
        }

    }

    public static class NullReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object ignored) {
            return null;
        }
    }

    public static class PrimitiveArrayReadHandler implements ReadHandler {

        public static final int INTS = 0;
        public static final int LONGS = 1;
        public static final int FLOATS = 2;
        public static final int DOUBLES = 3;
        public static final int BOOLS = 4;
        public static final int SHORTS = 5;
        public static final int CHARS = 6;

        private final int tag;

        public PrimitiveArrayReadHandler(int tag) {
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
        public Object fromRep(Object rep) {

            Object ret = null;
            switch(tag) {
                case INTS: ret = intArray((List<Long>) rep); break;
                case LONGS: ret = longArray((List<Long>) rep); break;
                case FLOATS: ret = floatArray((List<Double>) rep); break;
                case BOOLS: ret = boolArray((List<Boolean>) rep); break;
                case DOUBLES: ret = doubleArray((List<Double>) rep); break;
                case SHORTS: ret = shortArray((List<Long>) rep); break;
                case CHARS: ret = charArray((List<Long>) rep); break;
            }

            return ret;
        }
    }

    public static class RatioReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            List array = (List) rep;

            return new RatioImpl((BigInteger)array.get(0), (BigInteger)array.get(1));

        }
    }

    public static class SetReadHandler implements ArrayReadHandler {

        @Override
        public Object fromRep(Object o) { return o; }

        @Override
        public ArrayReader arrayReader() {
            return new ArrayReader() {
                @Override
                public Object init() {
                    return new HashSet();
                }

                @Override
                public Object init(int size) {
                    return new HashSet(size);
                }

                @Override
                public Object add(Object ar, Object item) {
                    ((Set)ar).add(item);
                    return ar;
                }

                @Override
                public Object complete(Object ar) {
                    return ar;
                }
            };
        }
    }

    public static class SymbolReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return TransitFactory.symbol((String) rep);
        }
    }

    public static class VerboseTimeReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            Calendar t = javax.xml.bind.DatatypeConverter.parseDateTime((String) rep);
            t.setTimeZone(TimeZone.getTimeZone("Zulu"));
            return t.getTime();
        }
    }

    public static class TimeReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            Long n;
            if (rep instanceof Long)
                n = (Long) rep;
            else
                n = Long.decode((String) rep);

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Zulu"));
            cal.setTimeInMillis(n);
            return cal.getTime();
        }
    }


    public static class URIReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return new URIImpl((String) rep);
        }
    }

    public static class UUIDReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            if(rep instanceof String) {
                return UUID.fromString((String) rep);
            }
            else {
                List<Long> l = (List<Long>) rep;
                return new UUID(l.get(0), l.get(1));
            }
        }
    }

    public static class LinkReadHandler implements ReadHandler {
        @Override
        public Object fromRep(Object rep) {
            return new LinkImpl((Map) rep);
        }
    }
}
