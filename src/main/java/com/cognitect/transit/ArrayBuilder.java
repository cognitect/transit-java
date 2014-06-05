// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.util.List;

public interface ArrayBuilder {
    Object init();
    Object init(int size);
    Object add(Object ab, Object item);
    List array(Object ab);
}
