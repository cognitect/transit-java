// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ReadHandlers {

    public static class BigDecimalReadHandler implements ReadHandler<Object, Object> {

        @Override
        public Object fromRep(Object rep) {
            return new BigDecimal((String)rep);
        }
    }

    public static class BinaryReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {

            return Base64.getDecoder().decode(rep.getBytes());
        }
    }

    public static class BooleanReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {
            return rep.equals("t");
        }
    }

    public static class CharacterReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {

            return rep.charAt(0);
        }
    }

    public static class CmapReadHandler implements ArrayReadHandler<Object, Map<Object, Object>, Object, Object> {

        @Override
        public Map<Object, Object> fromRep(Object objects) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayReader<Object,Map<Object, Object>,Object> arrayReader() {
            return new ArrayReader<Object, Map<Object, Object>, Object>() {
                private final Object MARKER = new Object();
                Map<Object, Object> m = null;
                Object next_key = MARKER;

                @Override
                public Object init() {
                    return init(16);
                }

                @Override
                public Object init(int size) {
                    m = new HashMap<Object, Object>(size);
                    return this;
                }

                @Override
                public Object add(Object ar, Object item) {
                    if (next_key != MARKER) {
                        m.put(next_key, item);
                        next_key = MARKER;
                    } else {
                        next_key = item;
                    }
                    return this;
                }

                @Override
                public Map<Object, Object> complete(Object ar) {
                    return m;
                }
            };
        }
    }

    public static class DoubleReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {

            return Double.valueOf(rep);
        }
    }

    public static class SpecialNumberReadHandler implements ReadHandler<Double, String> {
        @Override
        public Double fromRep(String rep) {
            if (rep.equals("NaN")) {
                return Double.NaN;
            } else if (rep.equals("INF")) {
                return Double.POSITIVE_INFINITY;
            } else if (rep.equals("-INF")) {
                return Double.NEGATIVE_INFINITY;
            } else {
                throw new RuntimeException();
            }
        }
    }

    public static class IdentityReadHandler implements ReadHandler<Object, Object> {

        @Override
        public Object fromRep(Object rep) {
            return rep;
        }
    }

    public static class IntegerReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {
            try {
                return Long.parseLong(rep);
            }catch(NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BigIntegerReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {
            return new BigInteger(rep);
        }
    }

    public static class KeywordReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {
            return TransitFactory.keyword(rep);
        }
    }

    public static class ListReadHandler implements ArrayReadHandler<List<Object>,List<Object>, Object, Object> {

        @Override
        public List<Object> fromRep(Object objects) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayReader<List<Object>, List<Object>, Object> arrayReader() {
            return new ArrayReader<List<Object>,List<Object>,Object>() {
                @Override
                public List<Object> init() {
                    return new LinkedList<Object>();
                }

                @Override
                public List<Object> init(int size) {
                    return init();
                }

                @Override
                public List<Object> add(List<Object> a, Object item) {
                    a.add(item);
                    return a;
                }

                @Override
                public List<Object> complete(List<Object> a) {
                    return a;
                }
            };
        }

    }

    public static class NullReadHandler implements ReadHandler<Object, Object> {

        @Override
        public Object fromRep(Object ignored) { return null; }
    }

    public static class RatioReadHandler implements ReadHandler<Object, List<BigInteger>> {

        @Override
        public Object fromRep(List<BigInteger> rep) {
            return new RatioImpl(rep.get(0), rep.get(1));
        }
    }

    public static class SetReadHandler implements ArrayReadHandler<Set<Object>,Set<Object>,Object, Object> {


        @Override
        public Set<Object> fromRep(Object objects) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayReader<Set<Object>, Set<Object>, Object> arrayReader() {
            return new ArrayReader<Set<Object>,Set<Object>,Object>() {
                @Override
                public Set<Object> init() {
                    return init(16);
                }

                @Override
                public Set<Object> init(int size) {
                    return new HashSet<Object>(size);
                }

                @Override
                public Set<Object> add(Set<Object> a, Object item) {
                    a.add(item);
                    return a;
                }

                @Override
                public Set<Object> complete(Set<Object> a) {
                    return a;
                }
            };
        }
    }

    public static class SymbolReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {
            return TransitFactory.symbol(rep);
        }
    }

    public static class VerboseTimeReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {
            Calendar t = javax.xml.bind.DatatypeConverter.parseDateTime(rep);
            t.setTimeZone(TimeZone.getTimeZone("Zulu"));
            return t.getTime();
        }
    }

    public static class TimeReadHandler implements ReadHandler<Object, Object> {

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


    public static class URIReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) { return new URIImpl(rep); }
    }

    public static class UUIDReadHandler implements ReadHandler<Object, Object> {

        @Override
        @SuppressWarnings("unchecked")
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

    public static class LinkReadHandler implements ReadHandler<Object, Map<String, String>> {
        @Override
        public Object fromRep(Map<String, String> rep) {
            return new LinkImpl(rep);
        }
    }
}
