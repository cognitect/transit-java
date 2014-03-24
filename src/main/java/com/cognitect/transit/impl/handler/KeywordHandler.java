package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;

public class KeywordHandler implements Handler {

    @Override
    public Tag tag(Object ignored) {
        return Tag.KEYWORD;
    }

    @Override
    public Object rep(Object o) {
        return o.toString();
    }

    @Override
    public String stringRep(Object o) {
        return o.toString();
    }
}
