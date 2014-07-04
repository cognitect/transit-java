// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ArrayReader;

import java.util.List;

public interface ArrayBuilder extends ArrayReader {
    @Override
    List complete(Object ar);
}
