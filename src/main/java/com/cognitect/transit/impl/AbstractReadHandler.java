// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;
import com.cognitect.transit.MapReader;
import com.cognitect.transit.ReadHandler;

public abstract class AbstractReadHandler implements ReadHandler {
    @Override
    public Object fromRep(Object rep) { return rep; }

    @Override
    public MapReader fromMapRep() {
        return null;
    }

    @Override
    public ArrayReader fromArrayRep() { return null; }
}