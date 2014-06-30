// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayBuilder;
import com.cognitect.transit.ListBuilder;
import com.cognitect.transit.MapBuilder;
import com.cognitect.transit.SetBuilder;

public interface ReaderSPI {
    public void setBuilders(MapBuilder mapBuilder,
                            ListBuilder listBuilder,
                            ArrayBuilder arrayBuilder,
                            SetBuilder setBuilder);
}
