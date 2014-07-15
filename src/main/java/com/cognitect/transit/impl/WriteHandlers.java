// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;
import com.cognitect.transit.Ratio;
import com.cognitect.transit.TaggedValue;
import com.cognitect.transit.TransitFactory;

import java.math.BigInteger;
import java.util.*;

public class WriteHandlers {
    public static class ArrayWriteHandler extends AbstractWriteHandler {

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

    public static class BinaryWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object ignored) {
            return "b";
        }

        @Override
        public Object rep(Object o) {
            return o;
        }
    }

    public static class BooleanWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object ignored) {
            return "?";
        }

        @Override
        public Object rep(Object o) {
            return o;
        }

        @Override
        public String stringRep(Object o) {
            return o.toString();
        }
    }

    public static class ListWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object o) {
            if (o instanceof RandomAccess)
                return "array";
            else if (o instanceof List)
                return "list";
            else
                throw new UnsupportedOperationException("Cannot marshal type as list: " + o.getClass().getSimpleName());
        }

        @Override
        public Object rep(Object o) {
            if (o instanceof LinkedList)
                return TransitFactory.taggedValue("array", o);
            else if (o instanceof List)
                return o;
            else
                throw new UnsupportedOperationException("Cannot marshal type as list: " + o.getClass().getSimpleName());
        }
    }

    public static class MapWriteHandler extends AbstractWriteHandler implements AbstractEmitterAware {

        private AbstractEmitter abstractEmitter;

        @Override
        public void setEmitter(AbstractEmitter abstractEmitter) {
            this.abstractEmitter = abstractEmitter;
        }

        private boolean stringableKeys(Map m) {

            Iterator i = m.keySet().iterator();
            while(i.hasNext()) {
                Object key = i.next();
                String tag = abstractEmitter.getTag(key);

                if(tag != null && tag.length() > 1)
                    return false;
                else if (tag == null && !(key instanceof String)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public String tag(Object o) {

            Map m = (Map)o;
            if(stringableKeys(m))
                return "map";
            else
                return "cmap";
        }

        @Override
        public Object rep(Object o) {

            Map m = (Map)o;
            if(stringableKeys(m)) {
                return ((Map)o).entrySet();
            }
            else {
                List l = new ArrayList(2*m.size());
                Iterator<Map.Entry> i = m.entrySet().iterator();
                while(i.hasNext()) {
                    Map.Entry e = i.next();
                    l.add(e.getKey());
                    l.add(e.getValue());
                }
                return TransitFactory.taggedValue("array", l);
            }
        }
    }

    public static class NullWriteHandler extends AbstractWriteHandler {

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

    public static class NumberWriteHandler extends AbstractWriteHandler {

        private final String t;

        public NumberWriteHandler(String t) {
            this.t = t;
        }

        @Override
        public String tag(Object ignored) {
            return t;
        }

        @Override
        public Object rep(Object o) {
            return o;
        }

        @Override
        public String stringRep(Object o) {
            return o.toString();
        }
    }

    public static class ObjectWriteHandler extends AbstractWriteHandler {

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

    public static class QuoteAbstractEmitter extends AbstractWriteHandler {

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

    public static class RatioWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object o) {
            return "ratio";
        }

        @Override
        public Object rep(Object o) {
            Ratio r = (Ratio)o;
            BigInteger[] l = {r.getNumerator(), r.getDenominator()};
            return TransitFactory.taggedValue("array", l);
        }
    }

    public static class SetWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object ignored) {
            return "set";
        }

        @Override
        public Object rep(Object o) {
            return TransitFactory.taggedValue("array", o);
        }
    }

    public static class TaggedValueWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object o) { return ((TaggedValue)o).getTag(); }

        @Override
        public Object rep(Object o) {
            return ((TaggedValue)o).getRep();
        }
    }

    public static class TimeWriteHandler extends AbstractWriteHandler {
        @Override
        public String tag(Object ignored) {
            return "m";
        }

        @Override
        public Object rep(Object o) {
            return ((Date)o).getTime();
        }

        @Override
        public String stringRep(Object o) {
            return rep(o).toString();
        }

        @Override
        public WriteHandler getVerboseHandler() {
            return new WriteHandler() {
                @Override
                public String tag(Object ignored) {
                    return "t";
                }

                @Override
                public Object rep(Object o) {
                    return AbstractParser.dateTimeFormat.format((Date)o);
                }

                @Override
                public String stringRep(Object o) {
                    return (String) rep(o);
                }

                @Override
                public WriteHandler getVerboseHandler() {
                    return this;
                }
            };
        }
    }

    public static class ToStringWriteHandler extends AbstractWriteHandler {

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

    public static class UUIDWriteHandler extends AbstractWriteHandler {

        @Override
        public String tag(Object ignored) {
            return "u";
        }

        @Override
        public Object rep(Object o) {
            UUID uuid = (UUID)o;
            List<Long> l = new ArrayList<Long>(2);
            l.add(uuid.getMostSignificantBits());
            l.add(uuid.getLeastSignificantBits());
            return l;
        }

        @Override
        public String stringRep(Object o) {
            return o.toString();
        }
    }

    public static class LinkWriteHandler extends AbstractWriteHandler {
        @Override
        public String tag(Object o) {
            return "link";
        }

        @Override
        public Object rep(Object o) {
            return ((LinkImpl)o).toMap();
        }
    }
}
