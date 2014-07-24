// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Reader;

public interface ReaderSPI<M,A> {
    public Reader setBuilders(MapBuilder<M> mapBuilder,
                              ArrayBuilder<A> arrayBuilder);
}
