// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.MapReader;

import java.util.Map;

public interface MapBuilder<G,K,V> extends MapReader<G, Map<K,V>,K,V> {
    @Override
    Map<K,V> complete(G m);
}
