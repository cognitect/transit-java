// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ListBuilder;

import java.util.ArrayList;
import java.util.List;

public class ListBuilderImpl implements ListBuilder {
    @Override
    public Object init() {
        return init(16);
    }

    @Override
    public Object init(int size) {
        return new ArrayList(size);
    }

    @Override
    public void add(Object ab, Object item) {
        ((List) ab).add(item);
    }

    @Override
    public List list(Object ab) {
        return (List) ab;
    }
}
