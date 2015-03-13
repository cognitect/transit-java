// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;

import java.math.BigInteger;
import java.util.*;

public class WriteHandlers {
    public static class ArrayWriteHandler extends AbstractWriteHandler<Object, Object> {

        public ArrayWriteHandler() {
        }

        @Override
        public String tag(Object ignored) {
            return "array";
        }

        @Override
        public Object rep(Object o) {
            return o;
        }
    }

    public static class BinaryWriteHandler extends AbstractWriteHandler<byte[], Object> {

        @Override
        public String tag(byte[] ignored) { return "b"; }

        @Override
        public Object rep(byte[] o) { return o; }
    }

    public static class BooleanWriteHandler extends AbstractWriteHandler<Boolean, Object> {

        @Override
        public String tag(Boolean ignored) {
            return "?";
        }

        @Override
        public Object rep(Boolean o) {
            return o;
        }

        @Override
        public String stringRep(Boolean o) {
            return o.toString();
        }
    }

    public static class KeywordWriteHandler extends AbstractWriteHandler<Keyword, Object> {

        @Override
        public String tag(Keyword ignored) {
            return ":";
        }

        @Override
        public Object rep(Keyword o) {
            return stringRep(o);
        }

        @Override
        public String stringRep(Keyword o) {
            return o.toString().substring(1);
        }
    }

    public static class ListWriteHandler extends AbstractWriteHandler<List<Object>, Object> {

        @Override
        public String tag(List<Object> o) {
            if (o instanceof RandomAccess) // ArrayList, Stack, Vector
                return "array";
            else
                return "list";
        }

        @Override
        public Object rep(List<Object> o) {
            if (o instanceof LinkedList)
                return TransitFactory.taggedValue("array", o);
            else
                return o;
        }
    }

    public static class MapWriteHandler extends AbstractWriteHandler<Map<Object, Object>, Object>
            implements TagProviderAware {

        private TagProvider tagProvider;

        @Override
        public void setTagProvider(TagProvider tagProvider) {
            this.tagProvider = tagProvider;
        }

        private boolean stringableKeys(Map<Object, Object> m) {

            Iterator<Object> i = m.keySet().iterator();
            while(i.hasNext()) {
                Object key = i.next();
                String tag = tagProvider.getTag(key);

                if(tag != null && tag.length() > 1)
                    return false;
                else if (tag == null && !(key instanceof String)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String tag(Map<Object, Object> o) {

            if(stringableKeys(o))
                return "map";
            else
                return "cmap";
        }

        @Override
        public Object rep(Map<Object, Object> o) {

            if(stringableKeys(o)) {
                return o.entrySet();
            }
            else {
                List<Object> l = new ArrayList<Object>(2*o.size());
                Iterator<Map.Entry<Object, Object>> i = o.entrySet().iterator();
                while(i.hasNext()) {
                    Map.Entry<Object, Object> e = i.next();
                    l.add(e.getKey());
                    l.add(e.getValue());
                }
                return TransitFactory.taggedValue("array", l);
            }
        }
    }

    public static class NullWriteHandler extends AbstractWriteHandler<Object, Object> {

        @Override
        public String tag(Object ignored) {
            return "_";
        }

        @Override
        public Object rep(Object ignored) {
            return null;
        }

        @Override
        public String stringRep(Object ignored) {
            return "";
        }
    }

    public static class FloatWriteHandler extends AbstractWriteHandler<Float, Object> {

        @Override
        public String tag(Float d) {
            if (d.isNaN() || d.isInfinite()) {
                return "z";
            } else {
                return "d";
            }
        }

        @Override
        public Object rep(Float d) {
            if (d.isNaN()) {
                return "NaN";
            } else if (d == Float.POSITIVE_INFINITY) {
                return "INF";
            } else if (d == Float.NEGATIVE_INFINITY) {
                return "-INF";
            } else {
                return d;
            }
        }

        @Override
        public String stringRep(Float d) {
            return this.rep(d).toString();
        }
    }

    public static class DoubleWriteHandler extends AbstractWriteHandler<Double, Object> {

        @Override
        public String tag(Double d) {
            if (d.isNaN() || d.isInfinite()) {
                return "z";
            } else {
                return "d";
            }
        }

        @Override
        public Object rep(Double d) {
            if (d.isNaN()) {
                return "NaN";
            } else if (d == Double.POSITIVE_INFINITY) {
                return "INF";
            } else if (d == Double.NEGATIVE_INFINITY) {
                return "-INF";
            } else {
                return d;
            }
        }

        @Override
        public String stringRep(Double d) {
            return this.rep(d).toString();
        }
    }

    public static class IntegerWriteHandler extends AbstractWriteHandler<Number, Object> {
        @Override
        public String tag(Number ignored) {
            return "i";
        }

        @Override
        public Number rep(Number o) {
            return o;
        }

        @Override
        public String stringRep(Number o) {
            return o.toString();
        }
    }

    public static class ObjectWriteHandler extends AbstractWriteHandler<Object, Object> {

        private String throwException(Object ignored) {
            throw new UnsupportedOperationException("Cannot marshal object of type " + ignored.getClass().getCanonicalName());
        }

        @Override
        public String tag(Object ignored) {
            return throwException(ignored);
        }

        @Override
        public Object rep(Object ignored) {
            return throwException(ignored);
        }

        @Override
        public String stringRep(Object ignored) {
            return throwException(ignored);
        }
    }

    public static class QuoteAbstractEmitter extends AbstractWriteHandler<Object, Object> {

        @Override
        public String tag(Object ignored) {
            return "'";
        }

        @Override
        public Object rep(Object o) {
            return ((Quote)o).o;
        }

        @Override
        public String stringRep(Object o) {
            throw new RuntimeException();
        }
    }

    public static class RatioWriteHandler extends AbstractWriteHandler<Ratio, Object> {

        @Override
        public String tag(Ratio o) {
            return "ratio";
        }

        @Override
        public Object rep(Ratio o) {
            Ratio r = (Ratio)o;
            List<BigInteger> l = new ArrayList<BigInteger>();
            l.add(r.getNumerator());
            l.add(r.getDenominator());
            return l;
        }
    }

    public static class SetWriteHandler extends AbstractWriteHandler<Set<Object>, Object> {

        @Override
        public String tag(Set<Object> ignored) {
            return "set";
        }

        @Override
        public Object rep(Set<Object> o) {
            return TransitFactory.taggedValue("array", o);
        }
    }

    public static class TaggedValueWriteHandler extends AbstractWriteHandler<TaggedValue, Object> {

        @Override
        public String tag(TaggedValue o) { return o.getTag(); }

        @Override
        public Object rep(TaggedValue o) { return o.getRep(); }
    }

    public static class TimeWriteHandler implements WriteHandler<Date, Object> {
        @Override
        public String tag(Date ignored) {
            return "m";
        }

        @Override
        public Object rep(Date o) { return o.getTime(); }

        @Override
        public String stringRep(Date o) {
            return rep(o).toString();
        }

        @Override
        @SuppressWarnings("unchecked")
        public WriteHandler<Date, String> getVerboseHandler() {
            return new WriteHandler<Date, String>() {
                @Override
                public String tag(Date ignored) {
                    return "t";
                }

                @Override
                public String rep(Date o) { return AbstractParser.dateTimeFormat.format(o); }

                @Override
                public String stringRep(Date o) {
                    return (String) rep(o);
                }

                @Override
                @SuppressWarnings("unchecked")
                public WriteHandler<Date, String> getVerboseHandler() {
                    return this;
                }
            };
        }
    }

    public static class ToStringWriteHandler extends AbstractWriteHandler<Object, Object> {

        private final String t;

        public ToStringWriteHandler(String t) {
            this.t = t;
        }

        @Override
        public String tag(Object ignored) {
            return t;
        }

        @Override
        public Object rep(Object o) {
            return o.toString();
        }

        @Override
        public String stringRep(Object o) {
            return (String) rep(o);
        }
    }

    public static class UUIDWriteHandler extends AbstractWriteHandler<UUID, Object> {

        @Override
        public String tag(UUID ignored) {
            return "u";
        }

        @Override
        public Object rep(UUID o) {
            UUID uuid = (UUID)o;
            long[] l = new long[2];
            l[0] = uuid.getMostSignificantBits();
            l[1] = uuid.getLeastSignificantBits();
            return l;
        }

        @Override
        public String stringRep(UUID o) {
            return o.toString();
        }
    }

    public static class LinkWriteHandler extends AbstractWriteHandler<LinkImpl, Object> {
        @Override
        public String tag(LinkImpl o) {
            return "link";
        }

        @Override
        public Object rep(LinkImpl o) {
            return o.toMap();
        }
    }
}
