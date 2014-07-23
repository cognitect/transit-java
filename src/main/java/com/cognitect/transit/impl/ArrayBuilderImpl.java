// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.ArrayList;
import java.util.List;

public class ArrayBuilderImpl<T> implements ArrayBuilder<T> {
    @Override
    public List<T> init() {
        return init(16);
    }

    @Override
    public List<T> init(int size) {
        return new ArrayList(size);
    }

    @Override
    public List<T> add(List<T> a, T item) {
        a.add(item);
        return a;
    }

    @Override
    public List<T> complete(List<T> a) {
        return a;
    }
}
