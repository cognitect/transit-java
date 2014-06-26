// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Handler;

public abstract class AbstractHandler implements Handler {

    @Override
    public String getStringRep(Object o) {
        return null;
    }

    @Override
    public Handler getVerboseHandler() {
        return null;
    }
}
