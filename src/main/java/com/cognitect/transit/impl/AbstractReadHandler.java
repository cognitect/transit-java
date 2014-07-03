// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayBuilder;
import com.cognitect.transit.MapBuilder;
import com.cognitect.transit.ReadHandler;

public abstract class AbstractReadHandler implements ReadHandler {

    @Override
    public MapBuilder fromMapRep() {
        return null;
    }

    @Override
    public ArrayBuilder fromArrayRep() {
        return null;
    }
}