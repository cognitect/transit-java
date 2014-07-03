// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ReadHandlers {

    public static class BigDecimalDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return new BigDecimal((String) rep);
        }
    }

    public static class BinaryDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return Base64.decodeBase64(((String) rep).getBytes());
        }
    }

    public static class BooleanDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return rep.equals("t");
        }
    }

    public static class CharacterDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return ((String) rep).charAt(0);
        }
    }

    public static class CmapDecoder extends AbstractReadHandler implements BuilderAware {

        private MapBuilder mapBuilder;

        @Override
        public Object fromRep(Object rep) {

            List array = (List) rep;

            Object mb = mapBuilder.init(array.size()/2);

            for(int i=0;i<array.size();i+=2) {
                mb = mapBuilder.add(mb, array.get(i), array.get(i + 1));
            }

            return mapBuilder.map(mb);
        }

        @Override
        public void setBuilders(MapBuilder mapBuilder, ListBuilder listBuilder,
                                ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
            this.mapBuilder = mapBuilder;
        }
    }

    public static class DoubleDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {

            return new Double((String) rep);
        }
    }

    public static class IdentityDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return rep;
        }
    }

    public static class IntegerDecoder extends AbstractReadHandler {

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

    public static class BigIntegerDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return new BigInteger((String) rep);
        }
    }

    public static class KeywordDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return TransitFactory.keyword((String) rep);
        }
    }

    public static class ListDecoder extends AbstractReadHandler implements BuilderAware {

        private ListBuilder listBuilder;

        @Override
        public Object fromRep(Object rep) {

            List array = (List) rep;

            Object lb = listBuilder.init(array.size());

            Iterator i = array.iterator();
            while(i.hasNext()) {
                lb = listBuilder.add(lb, i.next());
            }

            return listBuilder.list(lb);
        }

        @Override
        public void setBuilders(MapBuilder mapBuilder, ListBuilder listBuilder, ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
            this.listBuilder = listBuilder;
        }
    }

    public static class NullDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object ignored) {
            return null;
        }
    }

    public static class PrimitiveArrayDecoder extends AbstractReadHandler {

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

    public static class RatioDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {

            List array = (List) rep;

            return new RatioImpl((BigInteger)array.get(0), (BigInteger)array.get(1));

        }
    }

    public static class SetDecoder extends AbstractReadHandler implements BuilderAware {

        private SetBuilder setBuilder;

        @Override
        public Object fromRep(Object rep) {

            List list = (List) rep;

            Object sb = setBuilder.init(list.size());

            Iterator i = list.iterator();
            while(i.hasNext()) {
                sb = setBuilder.add(sb, i.next());
            }

            return setBuilder.set(sb);
        }

        @Override
        public void setBuilders(MapBuilder mapBuilder, ListBuilder listBuilder, ArrayBuilder arrayBuilder, SetBuilder setBuilder) {
            this.setBuilder = setBuilder;
        }
    }

    public static class SymbolDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return TransitFactory.symbol((String) rep);
        }
    }

    public static class VerboseTimeDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            Calendar t = javax.xml.bind.DatatypeConverter.parseDateTime((String) rep);
            t.setTimeZone(TimeZone.getTimeZone("Zulu"));
            return t.getTime();
        }
    }

    public static class TimeDecoder extends AbstractReadHandler {

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


    public static class URIDecoder extends AbstractReadHandler {

        @Override
        public Object fromRep(Object rep) {
            return new URIImpl((String) rep);
        }
    }

    public static class UUIDDecoder extends AbstractReadHandler {

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

    public static class LinkDecoder extends AbstractReadHandler {
        @Override
        public Object fromRep(Object rep) {
            return new LinkImpl((Map) rep);
        }
    }
}
