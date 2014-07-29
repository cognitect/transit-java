// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.SPI;

import com.cognitect.transit.ArrayReader;
import com.cognitect.transit.MapReader;
import com.cognitect.transit.Reader;

import java.util.List;
import java.util.Map;

public interface ReaderSPI {
    public Reader setBuilders(MapReader<?, Map<Object, Object>, Object, Object> mapBuilder,
                              ArrayReader<?, List<Object>, Object> listBuilder);
}
