package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

public class BinaryHandler implements Handler {

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
