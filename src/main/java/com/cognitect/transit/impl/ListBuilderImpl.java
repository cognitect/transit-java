// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ListBuilder;

import java.util.LinkedList;
import java.util.List;

public class ListBuilderImpl implements ListBuilder {
    @Override
    public Object init() { return init(16); }

    @Override
    public Object init(int size) { return new LinkedList(); }

    @Override
    public Object add(Object lb, Object item) {
        ((List) lb).add(item);
        return lb;
    }

    @Override
    public List list(Object lb) {
        return (List) lb;
    }
}
