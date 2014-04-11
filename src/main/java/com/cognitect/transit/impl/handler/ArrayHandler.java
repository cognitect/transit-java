// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AsTag;

public class ArrayHandler implements Handler {

    private final String tag;

    public ArrayHandler(String tag) {
        this.tag = tag;
    }

    @Override
    public String tag(Object ignored) {
        return tag;
    }

    @Override
    public Object rep(Object o) {
        if(tag.equals("array"))
            return o;
        else
            return new AsTag("array", o, null);
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
