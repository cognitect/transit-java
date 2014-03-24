package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;

public class NullHandler implements Handler {

    @Override
    public Tag tag(Object ignored) {
        return Tag.NULL;
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
