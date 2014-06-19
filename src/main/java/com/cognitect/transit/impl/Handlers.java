// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.TaggedValue;
import com.cognitect.transit.TransitFactory;

import java.util.*;

public class Handlers{
public static class ArrayHandler extends AbstractHandler {

    private final String tag;

    public ArrayHandler(String tag) {
        this.tag = tag;
    }

    @Override
    public String tag(Object ignored) {
        return tag;
    }

    @Override
    public Object rep(Object o) {
        if(tag.equals("array"))
            return o;
        else
            return TransitFactory.taggedValue("array", o);
    }
}

public static class BinaryHandler extends AbstractHandler {

    @Override
    public String tag(Object ignored) {
        return "b";
    }

    @Override
    public Object rep(Object o) {
        return o;
    }
}

public static class BooleanHandler extends AbstractHandler {

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

public static class ListHandler extends AbstractHandler {

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

public static class MapAbstractEmitter extends AbstractHandler implements AbstractEmitterAware {

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

public static class NullHandler extends AbstractHandler {

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

public static class NumberHandler extends AbstractHandler {

    private final String t;

    public NumberHandler(String t) {
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

public static class ObjectHandler extends AbstractHandler {

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

public static class QuoteAbstractEmitter extends AbstractHandler {

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

public static class RatioHandler extends AbstractHandler {

    @Override
    public String tag(Object o) {
        return "ratio";
    }

    @Override
    public Object rep(Object o) {
        Ratio r = (Ratio)o;
        long[] l = {r.numerator, r.denominator};
        return TransitFactory.taggedValue("array", l);
    }
}

public static class SetHandler extends AbstractHandler {

    @Override
    public String tag(Object ignored) {
        return "set";
    }

    @Override
    public Object rep(Object o) {
        return TransitFactory.taggedValue("array", o, null);
    }
}

public static class TaggedValueHandler extends AbstractHandler {

    @Override
    public String tag(Object o) { return ((TaggedValue)o).getTag(); }

    @Override
    public Object rep(Object o) {
        return ((TaggedValue)o).getRep();
    }
}

public static class TimeHandler extends AbstractHandler {
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
    public Handler verboseHandler() {
        return new Handler() {
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
                return (String)rep(o);
            }

            @Override
            public Handler verboseHandler() {
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
    public String tag(Object ignored) {
        return t;
    }

    @Override
    public Object rep(Object o) {
        return o.toString();
    }

    @Override
    public String stringRep(Object o) {
        return (String)rep(o);
    }
}

public static class UUIDHandler extends AbstractHandler {

    @Override
    public String tag(Object ignored) {
        return "u";
    }

    @Override
    public Object rep(Object o) {
        UUID uuid = (UUID)o;
        List<Long> l = new ArrayList<Long>(2);
        l.add(uuid.getLeastSignificantBits());
        l.add(uuid.getLeastSignificantBits());
        return l;
    }

    @Override
    public String stringRep(Object o) {
        return o.toString();
    }
}
}
