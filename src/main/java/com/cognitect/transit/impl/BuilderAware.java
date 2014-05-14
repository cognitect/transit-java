// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ListBuilder;
import com.cognitect.transit.MapBuilder;

public interface BuilderAware {
    void setMapBuilder(MapBuilder mapBuilder);
    void setListBuilder(ListBuilder listBuilder);
}
