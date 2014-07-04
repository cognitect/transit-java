// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.MapReader;

import java.util.Map;

public interface MapBuilder extends MapReader {
    @Override
    Map complete(Object mb);
}
