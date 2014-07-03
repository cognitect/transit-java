// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayBuilder;

import java.util.ArrayList;
import java.util.List;

public class ArrayBuilderImpl implements ArrayBuilder {
    @Override
    public Object init() {
        return init(16);
    }

    @Override
    public Object init(int size) {
        return new ArrayList(size);
    }

    @Override
    public Object add(Object ab, Object item) {
        ((List) ab).add(item);
        return ab;
    }

    @Override
    public List array(Object ab) {
        return (List) ab;
    }
}
