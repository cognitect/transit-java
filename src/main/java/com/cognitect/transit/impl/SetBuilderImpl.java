// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.SetBuilder;

import java.util.HashSet;
import java.util.Set;

public class SetBuilderImpl implements SetBuilder {
    @Override
    public Object init() {
        return init(16);
    }

    @Override
    public Object init(int size) { return new HashSet(size); }

    @Override
    public Object add(Object sb, Object item) {
        ((Set) sb).add(item);
        return sb;
    }

    @Override
    public Set set(Object sb) {
        return (Set) sb;
    }
}
