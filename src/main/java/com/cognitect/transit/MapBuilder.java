// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.Map;

public interface MapBuilder {
    Object init();
    Object init(int size);
    Object add(Object mb, Object key, Object value);
    Map map(Object mb);
}
