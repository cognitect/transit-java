// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.List;

public interface ListBuilder {
    Object init();
    Object init(int size);
    void add(Object mb, Object item);
    List list(Object mb);
}
