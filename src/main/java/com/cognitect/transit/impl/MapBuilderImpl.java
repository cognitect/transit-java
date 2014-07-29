// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.MapReader;

import java.util.HashMap;
import java.util.Map;

public class MapBuilderImpl implements MapReader<Map<Object, Object>, Map<Object, Object>, Object, Object> {
    @Override
    public Map<Object, Object> init() {
        return init(16);
    }

    @Override
    public Map<Object, Object> init(int size) {
        return new HashMap<Object, Object>(size);
    }

    @Override
    public Map<Object, Object> add(Map<Object, Object> m, Object key, Object value) {
        m.put(key, value);
        return m;
    }

    @Override
    public Map<Object, Object> complete(Map<Object, Object> m) {
        return m;
    }
}
