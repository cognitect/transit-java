// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AsTag;

import java.util.List;

public class ListHandler implements Handler {

    @Override
    public String tag(Object o) {
        List l = (List)o;
        return "list";
    }

    @Override
    public Object rep(Object o) {
        List l = (List)o;
        return new AsTag("array", l, null);
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
