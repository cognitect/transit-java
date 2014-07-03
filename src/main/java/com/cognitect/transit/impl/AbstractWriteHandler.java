// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.WriteHandler;

public abstract class AbstractWriteHandler implements WriteHandler {

    @Override
    public String getStringRep(Object o) {
        return null;
    }

    @Override
    public WriteHandler getVerboseHandler() {
        return null;
    }
}
