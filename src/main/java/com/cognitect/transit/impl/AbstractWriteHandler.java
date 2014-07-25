// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;

public abstract class AbstractWriteHandler<T, Rep> implements WriteHandler<T, Rep> {

    @Override
    public String stringRep(T o) {
        return null;
    }

    @Override
    public WriteHandler<T, Object> getVerboseHandler() {
        return null;
    }
}
