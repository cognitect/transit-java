// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.*;

public interface ReaderSPI {
    public Reader setBuilders(MapBuilder mapBuilder,
                              ListBuilder listBuilder,
                              ArrayBuilder arrayBuilder,
                              SetBuilder setBuilder);
}
