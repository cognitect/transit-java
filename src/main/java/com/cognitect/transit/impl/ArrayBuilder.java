// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;

import java.util.List;

public interface ArrayBuilder<G> extends ArrayReader<G, List<Object>, Object> {
    @Override
    List<Object> complete(G ar);
}
