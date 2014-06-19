// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;

public abstract class AbstractHandler implements Handler {

    @Override
    public String stringRep(Object o) {
        return null;
    }

    @Override
    public Handler verboseHandler() {
        return null;
    }
}
