// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import java.io.IOException;

public interface Parser {
    Object parse(ReadCache cache) throws IOException;
    Object parseVal(boolean asMapKey, ReadCache cache) throws IOException;
    Object parseMap(boolean asMapKey, ReadCache cache) throws IOException;
    Object parseArray(boolean asMapKey, ReadCache cache) throws IOException;
}
