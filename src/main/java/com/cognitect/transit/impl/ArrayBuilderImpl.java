// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.util.ArrayList;
import java.util.List;

public class ArrayBuilderImpl implements ArrayBuilder<List> {
    @Override
    public List init() {
        return init(16);
    }

    @Override
    public List init(int size) {
        return new ArrayList(size);
    }

    @Override
    public List add(List a, Object item) {
        a.add(item);
        return a;
    }

    @Override
    public List complete(List a) {
        return a;
    }
}
