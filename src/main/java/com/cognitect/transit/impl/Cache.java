// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> extends LinkedHashMap<K, V> {

    public Cache() {
        super(10);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > 10;
    }
}
