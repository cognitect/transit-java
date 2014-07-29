// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;

import java.util.ArrayList;
import java.util.List;

public class ListBuilderImpl implements ArrayReader<List<Object>, List<Object>, Object> {
    @Override
    public List<Object> init() {
        return init(16);
    }

    @Override
    public List<Object> init(int size) {
        return new ArrayList<Object>(size);
    }

    @Override
    public List<Object> add(List<Object> a, Object item) {
        a.add(item);
        return a;
    }

    @Override
    public List<Object> complete(List<Object> a) {
        return a;
    }
}
