package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;

public class ToStringHandler implements Handler {

    private final Tag t;

    public ToStringHandler(Tag t) {
        this.t = t;
    }

    @Override
    public Tag tag(Object ignored) {
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
