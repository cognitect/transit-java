// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

public class NullHandler implements Handler {

    @Override
    public String tag(Object ignored) {
        return "_";
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
