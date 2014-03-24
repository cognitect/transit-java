package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;

public class BinaryHandler implements Handler {

    @Override
    public Tag tag(Object ignored) {
        return Tag.BINARY;
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
