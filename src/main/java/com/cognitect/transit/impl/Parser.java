// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.ReadHandler;

import java.io.IOException;

public interface Parser {
    Object parse(ReadCache cache) throws IOException;
    Object parseVal(boolean asMapKey, ReadCache cache, ReadHandler handler) throws IOException;
    Object parseMap(boolean asMapKey, ReadCache cache, ReadHandler handler) throws IOException;
    Object parseArray(boolean asMapKey, ReadCache cache, ReadHandler handler) throws IOException;
}
