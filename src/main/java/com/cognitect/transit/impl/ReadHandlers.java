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

    public static class CmapReadHandler implements ArrayReadHandler<Map,Map,Object> {

        @Override
        public Object fromRep(Object o) { throw new UnsupportedOperationException(); }

        @Override
        public ArrayReader<Map,Map,Object> arrayReader() {
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

    public static class ListReadHandler implements ArrayReadHandler<List,List,Object> {

        @Override
        public Object fromRep(Object o) { throw new UnsupportedOperationException(); }

        @Override
        public ArrayReader arrayReader() {
            return new ArrayReader<List,List,Object>() {
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

    public static class NullReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object ignored) {
            return null;
        }
    }

    public static class RatioReadHandler implements ReadHandler {

        @Override
        public Object fromRep(Object rep) {

            List array = (List) rep;

            return new RatioImpl((BigInteger)array.get(0), (BigInteger)array.get(1));

        }
    }

    public static class SetReadHandler implements ArrayReadHandler<Set,Set,Object> {

        @Override
        public Object fromRep(Object o) { throw new UnsupportedOperationException();}

        @Override
        public ArrayReader arrayReader() {
            return new ArrayReader<Set,Set,Object>() {
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
