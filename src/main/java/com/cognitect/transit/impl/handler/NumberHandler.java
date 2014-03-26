package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

public class NumberHandler implements Handler {

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
