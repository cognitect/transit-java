// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.HashMap;
import java.util.Map;

public class MapBuilderImpl implements MapBuilder<Map<Object, Object>> {
    @Override
    public Map init() {
        return init(16);
    }

    @Override
    public Map init(int size) {
        return new HashMap(size);
    }

    @Override
    public Map add(Map m, Object key, Object value) {
        m.put(key, value);
        return m;
    }

    @Override
    public Map complete(Map m) {
        return m;
    }
}
