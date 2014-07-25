// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ReadHandlers {

    public static class BigDecimalReadHandler implements ReadHandler<BigDecimal, String> {

        @Override
        public BigDecimal fromRep(String rep) {
            return new BigDecimal(rep);
        }
    }

    public static class BinaryReadHandler implements ReadHandler<Object, String> {

        @Override
        public Object fromRep(String rep) {

            return Base64.decodeBase64(rep.getBytes());
        }
    }

    public static class BooleanReadHandler implements ReadHandler<Boolean, String> {

        @Override
        public Boolean fromRep(String rep) {

            return rep.equals("t");
        }
    }

    public static class CharacterReadHandler implements ReadHandler<Character, String> {

        @Override
        public Character fromRep(String rep) {

            return rep.charAt(0);
        }
    }

    public static class CmapReadHandler implements ArrayReadHandler<ArrayReader,Map<Object, Object>,Object> {

        @Override
        public Map<Object, Object> fromRep(List<Object> objects) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayReader<ArrayReader,Map<Object, Object>,Object> arrayReader() {
            return new ArrayReader<ArrayReader, Map<Object, Object>, Object>() {
                Map m = null;
                Object next_key = null;

                @Override
                public ArrayReader init() {
                    m = new HashMap();
                    return this;
                }

                @Override
                public ArrayReader init(int size) {
                    m = new HashMap(size);
                    return this;
                }

                @Override
                public ArrayReader add(ArrayReader ar, Object item) {
                    if (next_key != null) {
                        m.put(next_key, item);
                        next_key = null;
                    } else {
                        next_key = item;
                    }
                    return this;
                }

                @Override
                public Map complete(ArrayReader ar) {
                    return m;
                }
            };
        }
    }

    public static class DoubleReadHandler implements ReadHandler<Double, String> {

        @Override
        public Double fromRep(String rep) {

            return new Double(rep);
        }
    }

    public static class IdentityReadHandler implements ReadHandler<Object, Object> {

        @Override
        public Object fromRep(Object rep) {
            return rep;
        }
    }

    public static class IntegerReadHandler implements ReadHandler<Long, String> {

        @Override
        public Long fromRep(String rep) {
            try {
                return Long.parseLong(rep);
            }catch(NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class BigIntegerReadHandler implements ReadHandler<BigInteger, String> {

        @Override
        public BigInteger fromRep(String rep) {
            return new BigInteger(rep);
        }
    }

    public static class KeywordReadHandler implements ReadHandler<Keyword, String> {

        @Override
        public Keyword fromRep(String rep) {
            return TransitFactory.keyword(rep);
        }
    }

    public static class ListReadHandler implements ArrayReadHandler<List<Object>,List<Object>,Object> {

        @Override
        public List<Object> fromRep(List<Object> objects) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayReader<List<Object>, List<Object>, Object> arrayReader() {
            return new ArrayReader<List<Object>,List<Object>,Object>() {
                @Override
                public List init() {
                    return new LinkedList();
                }

                @Override
                public List init(int size) {
                    return init();
                }

                @Override
                public List add(List a, Object item) {
                    a.add(item);
                    return a;
                }

                @Override
                public List complete(List a) {
                    return a;
                }
            };
        }

    }

    public static class NullReadHandler implements ReadHandler<Object, Object> {

        @Override
        public Object fromRep(Object ignored) { return null; }
    }

    public static class RatioReadHandler implements ReadHandler<Ratio, List<BigInteger>> {

        @Override
        public Ratio fromRep(List<BigInteger> rep) {
            return new RatioImpl(rep.get(0), rep.get(1));
        }
    }

    public static class SetReadHandler implements ArrayReadHandler<Set<Object>,Set<Object>,Object> {


        @Override
        public Set<Object> fromRep(List<Object> objects) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayReader<Set<Object>, Set<Object>, Object> arrayReader() {
            return new ArrayReader<Set<Object>,Set<Object>,Object>() {
                @Override
                public Set init() {
                    return new HashSet();
                }

                @Override
                public Set init(int size) {
                    return new HashSet(size);
                }

                @Override
                public Set add(Set a, Object item) {
                    a.add(item);
                    return a;
                }

                @Override
                public Set complete(Set a) {
                    return a;
                }
            };
        }
    }

    public static class SymbolReadHandler implements ReadHandler<Symbol, String> {

        @Override
        public Symbol fromRep(String rep) {
            return TransitFactory.symbol(rep);
        }
    }

    public static class VerboseTimeReadHandler implements ReadHandler<Date, String> {

        @Override
        public Date fromRep(String rep) {
            Calendar t = javax.xml.bind.DatatypeConverter.parseDateTime(rep);
            t.setTimeZone(TimeZone.getTimeZone("Zulu"));
            return t.getTime();
        }
    }

    public static class TimeReadHandler implements ReadHandler<Date, Object> {

        @Override
        public Date fromRep(Object rep) {
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


    public static class URIReadHandler implements ReadHandler<URI, String> {

        @Override
        public URI fromRep(String rep) { return new URIImpl(rep); }
    }

    public static class UUIDReadHandler implements ReadHandler<UUID, Object> {

        @Override
        public UUID fromRep(Object rep) {

            if(rep instanceof String) {
                return UUID.fromString((String) rep);
            }
            else {
                List<Long> l = (List<Long>) rep;
                return new UUID(l.get(0), l.get(1));
            }
        }
    }

    public static class LinkReadHandler implements ReadHandler<Link, Map<String, String>> {
        @Override
        public Link fromRep(Map<String, String> rep) {
            return new LinkImpl(rep);
        }
    }
}
