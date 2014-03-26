package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

import java.util.Map;

public class MapHandler implements Handler {

    @Override
    public String tag(Object ignored) {
        return "map";
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
