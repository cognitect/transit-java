// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

public class ToStringHandler implements Handler {

    private final String t;

    public ToStringHandler(String t) {
        this.t = t;
    }

    @Override
    public String tag(Object ignored) {
        return t;
    }

    @Override
    public Object rep(Object o) {
        return o.toString();
    }

    @Override
    public String stringRep(Object o) {
        return (String)rep(o);
    }
}
