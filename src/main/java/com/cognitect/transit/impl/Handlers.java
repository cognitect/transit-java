// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Ratio;
import com.cognitect.transit.TaggedValue;
import com.cognitect.transit.TransitFactory;

import java.util.*;

public class Handlers{
public static class ArrayHandler implements Handler{

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

    @Override
    public String stringRep(Object o) {
        return null;
    }
}

public static class BinaryHandler implements Handler {

    @Override
    public String tag(Object ignored) {
        return "b";
    }

    @Override
    public Object rep(Object o) {
        return o;
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}

public static class BooleanHandler implements Handler {

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

public static class ListHandler implements Handler {

    @Override
    public String tag(Object o) {
        if (o instanceof LinkedList)
            return "list";
        else if (o instanceof List)
            return "array";
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

    @Override
    public String stringRep(Object o) {
        return null;
    }
}

public static class MapHandler implements Handler, HandlerAware {

    private Handler handler;

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private boolean stringableKeys(Map m) {

        Iterator i = m.keySet().iterator();
        while(i.hasNext()) {
            Object key = i.next();
            String tag = handler.tag(key);
            if(tag.length() > 1)
                return false;
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

    @Override
    public String stringRep(Object o) {
        return null;
    }
}

public static class NullHandler implements Handler {

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
        return "null";
    }
}

public static class NumberHandler implements Handler {

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

public static class ObjectHandler implements Handler {

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

public static class QuoteHandler implements Handler, HandlerAware {

    private Handler handler;

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

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
        System.out.println("THIS SHOULD NEVER BE CALLED");
        return handler.stringRep(o);
    }


}

public static class RatioHandler implements Handler {

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

    @Override
    public String stringRep(Object o) {
        return null;
    }
}

public static class SetHandler implements Handler {

    @Override
    public String tag(Object ignored) {
        return "set";
    }

    @Override
    public Object rep(Object o) {
        return TransitFactory.taggedValue("array", o, null);
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}

public static class TaggedValueHandler implements Handler {

    @Override
    public String tag(Object o) { return ((TaggedValue)o).getTag(); }

    @Override
    public Object rep(Object o) {
        return ((TaggedValue)o).getRep();
    }

    @Override
    public String stringRep(Object ignored) {
        return null;
    }
}

public static class TimeHandler implements Handler {

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
}

public static class ToStringHandler implements Handler {

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

public static class UUIDHandler implements Handler {

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
