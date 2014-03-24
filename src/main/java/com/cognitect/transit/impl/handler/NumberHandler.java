package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;

public class NumberHandler implements Handler {

    private final Tag t;

    public NumberHandler(Tag t) {
        this.t = t;
    }

    @Override
    public Tag tag(Object ignored) {
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
