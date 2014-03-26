package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AsTag;

import java.util.List;

public class ListHandler implements Handler {

    @Override
    public String tag(Object o) {
        List l = (List)o;
        if(l.size() > 0)
            return "list";
        else
            return "array";
    }

    @Override
    public Object rep(Object o) {
        List l = (List)o;
        if(l.size() > 0)
            return new AsTag("array", l, null);
        else
            return o;
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
