package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Tag;

import java.util.Map;

public class MapHandler implements Handler {

    @Override
    public Tag tag(Object ignored) {
        return Tag.MAP;
    }

    @Override
    public Object rep(Object o) {
        return ((Map)o).entrySet();
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
