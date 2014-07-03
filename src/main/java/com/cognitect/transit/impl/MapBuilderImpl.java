// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.HashMap;
import java.util.Map;

public class MapBuilderImpl implements MapBuilder {
    @Override
    public Object init() {
        return init(16);
    }

    @Override
    public Object init(int size) {
        return new HashMap(size);
    }

    @Override
    public Object add(Object mb, Object key, Object value) {
        ((Map) mb).put(key, value);
        return mb;
    }

    @Override
    public Map map(Object mb) {
        return (Map) mb;
    }
}
