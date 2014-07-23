// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.MapReader;

import java.util.HashMap;
import java.util.Map;

public class MapBuilderImpl<K,V> implements MapBuilder<Map<K,V>, K,V> {
    @Override
    public Map<K,V> init() {
        return init(16);
    }

    @Override
    public Map<K,V> init(int size) {
        return new HashMap(size);
    }

    @Override
    public Map<K,V> add(Map<K,V> m, K key, V value) {
        ((Map) m).put(key, value);
        return m;
    }

    @Override
    public Map<K,V> complete(Map<K,V> m) {
        return (Map) m;
    }
}
