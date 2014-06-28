// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Ratio;
import com.cognitect.transit.TaggedValue;
import com.cognitect.transit.TransitFactory;

import java.math.BigInteger;
import java.util.*;

public class Handlers {
    public static class ArrayHandler extends AbstractHandler {

        private final String tag;

        public ArrayHandler(String tag) {
            this.tag = tag;
        }

        @Override
        public String getTag(Object ignored) {
            return tag;
        }

        @Override
        public Object getRep(Object o) {
            if(tag.equals("array"))
                return o;
            else
                return TransitFactory.taggedValue("array", o);
        }
    }

    public static class BinaryHandler extends AbstractHandler {

        @Override
        public String getTag(Object ignored) {
            return "b";
        }

        @Override
        public Object getRep(Object o) {
            return o;
        }
    }

    public static class BooleanHandler extends AbstractHandler {

        @Override
        public String getTag(Object ignored) {
            return "?";
        }

        @Override
        public Object getRep(Object o) {
            return o;
        }

        @Override
        public String getStringRep(Object o) {
            return o.toString();
        }
    }

    public static class ListHandler extends AbstractHandler {

        @Override
        public String getTag(Object o) {
            if (o instanceof RandomAccess)
                return "array";
            else if (o instanceof List)
                return "list";
            else
                throw new UnsupportedOperationException("Cannot marshal type as list: " + o.getClass().getSimpleName());
        }

        @Override
        public Object getRep(Object o) {
            if (o instanceof LinkedList)
                return TransitFactory.taggedValue("array", o);
            else if (o instanceof List)
                return o;
            else
                throw new UnsupportedOperationException("Cannot marshal type as list: " + o.getClass().getSimpleName());
        }
    }

    public static class MapHandler extends AbstractHandler implements AbstractEmitterAware {

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
        public String getTag(Object o) {

            Map m = (Map)o;
            if(stringableKeys(m))
                return "map";
            else
                return "cmap";
        }

        @Override
        public Object getRep(Object o) {

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

    public static class NullHandler extends AbstractHandler {

        @Override
        public String getTag(Object ignored) {
            return "_";
        }

        @Override
        public Object getRep(Object ignored) {
            return null;
        }

        @Override
        public String getStringRep(Object ignored) {
            return "";
        }
    }

    public static class NumberHandler extends AbstractHandler {

        private final String t;

        public NumberHandler(String t) {
            this.t = t;
        }

        @Override
        public String getTag(Object ignored) {
            return t;
        }

        @Override
        public Object getRep(Object o) {
            return o;
        }

        @Override
        public String getStringRep(Object o) {
            return o.toString();
        }
    }

    public static class ObjectHandler extends AbstractHandler {

        private String throwException(Object ignored) {
            throw new UnsupportedOperationException("Cannot marshal object of type " + ignored.getClass().getCanonicalName());
        }

        @Override
        public String getTag(Object ignored) {
            return throwException(ignored);
        }

        @Override
        public Object getRep(Object ignored) {
            return throwException(ignored);
        }

        @Override
        public String getStringRep(Object ignored) {
            return throwException(ignored);
        }
    }

    public static class QuoteAbstractEmitter extends AbstractHandler {

        @Override
        public String getTag(Object ignored) {
            return "'";
        }

        @Override
        public Object getRep(Object o) {
            return ((Quote)o).o;
        }

        @Override
        public String getStringRep(Object o) {
            throw new RuntimeException();
        }
    }

    public static class RatioHandler extends AbstractHandler {

        @Override
        public String getTag(Object o) {
            return "ratio";
        }

        @Override
        public Object getRep(Object o) {
            Ratio r = (Ratio)o;
            BigInteger[] l = {r.getNumerator(), r.getDenominator()};
            return TransitFactory.taggedValue("array", l);
        }
    }

    public static class SetHandler extends AbstractHandler {

        @Override
        public String getTag(Object ignored) {
            return "set";
        }

        @Override
        public Object getRep(Object o) {
            return TransitFactory.taggedValue("array", o, null);
        }
    }

    public static class TaggedValueHandler extends AbstractHandler {

        @Override
        public String getTag(Object o) { return ((TaggedValue)o).getTag(); }

        @Override
        public Object getRep(Object o) {
            return ((TaggedValue)o).getRep();
        }

        @Override
        public String getStringRep(Object o) { return ((TaggedValue)o).getStringRep(); }
    }

    public static class TimeHandler extends AbstractHandler {
        @Override
        public String getTag(Object ignored) {
            return "m";
        }

        @Override
        public Object getRep(Object o) {
            return ((Date)o).getTime();
        }

        @Override
        public String getStringRep(Object o) {
            return getRep(o).toString();
        }

        @Override
        public Handler getVerboseHandler() {
            return new Handler() {
                @Override
                public String getTag(Object ignored) {
                    return "t";
                }

                @Override
                public Object getRep(Object o) {
                    return AbstractParser.dateTimeFormat.format((Date)o);
                }

                @Override
                public String getStringRep(Object o) {
                    return (String)getRep(o);
                }

                @Override
                public Handler getVerboseHandler() {
                    return this;
                }
            };
        }
    }

    public static class ToStringHandler extends AbstractHandler {

        private final String t;

        public ToStringHandler(String t) {
            this.t = t;
        }

        @Override
        public String getTag(Object ignored) {
            return t;
        }

        @Override
        public Object getRep(Object o) {
            return o.toString();
        }

        @Override
        public String getStringRep(Object o) {
            return (String)getRep(o);
        }
    }

    public static class UUIDHandler extends AbstractHandler {

        @Override
        public String getTag(Object ignored) {
            return "u";
        }

        @Override
        public Object getRep(Object o) {
            UUID uuid = (UUID)o;
            List<Long> l = new ArrayList<Long>(2);
            l.add(uuid.getMostSignificantBits());
            l.add(uuid.getLeastSignificantBits());
            return l;
        }

        @Override
        public String getStringRep(Object o) {
            return o.toString();
        }
    }

    public static class LinkHandler extends AbstractHandler {
        @Override
        public String getTag(Object o) {
            return "link";
        }

        @Override
        public Object getRep(Object o) {
            return ((LinkImpl)o).toList();
        }
    }
}
