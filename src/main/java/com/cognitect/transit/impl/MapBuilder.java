// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.MapReader;

import java.util.Map;

public interface MapBuilder<G> extends MapReader<G, Map<Object, Object>, Object, Object> {
    @Override
    Map<Object, Object> complete(G m);
}
