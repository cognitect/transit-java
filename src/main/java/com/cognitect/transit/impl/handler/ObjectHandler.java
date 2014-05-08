// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;

public class ObjectHandler implements Handler {

    private String throwException(Object ignored) {
        throw new UnsupportedOperationException("Cannot marshal object of type " + ignored.getClass().getCanonicalName());
    }

    @Override
    public String tag(Object ignored) {
        return throwException(ignored);
    }

    @Override
    public Object rep(Object ignored) {
        return throwException(ignored);
    }

    @Override
    public String stringRep(Object ignored) {
        return throwException(ignored);
    }
}
