// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.Set;

public interface SetBuilder {
    Object init();
    Object init(int size);
    void add(Object sb, Object item);
    Set set(Object sb);
}
