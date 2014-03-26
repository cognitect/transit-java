package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AsTag;

public class AsTagHandler implements Handler {

    @Override
    public String tag(Object o) {
        return ((AsTag)o).tag;
    }

    @Override
    public Object rep(Object o) {
        return ((AsTag)o).rep;
    }

    @Override
    public String stringRep(Object o) {
        return ((AsTag)o).repString;
    }
}
