// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AsTag;

import java.util.LinkedList;
import java.util.List;

public class ListHandler implements Handler {

    @Override
    public String tag(Object o) {
        if (o instanceof LinkedList)
            return "list";
        else if (o instanceof List)
            return "array";
        else
            throw new UnsupportedOperationException("Cannot marshal type as list: " + o.getClass().getSimpleName());
    }

    @Override
    public Object rep(Object o) {
        if (o instanceof LinkedList)
            return new AsTag("array", o, null);
        else if (o instanceof List)
            return o;
        else
            throw new UnsupportedOperationException("Cannot marshal type as list: " + o.getClass().getSimpleName());
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
