// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;

import java.util.List;

public interface ArrayBuilder<T> extends ArrayReader<List<T>, List<T>, T> {
    @Override
    List<T> complete(List<T> ar);
}
