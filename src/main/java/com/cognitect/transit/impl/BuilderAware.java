// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

public interface BuilderAware {
    void setBuilders(MapBuilder mapBuilder, ListBuilder listBuilder, ArrayBuilder arrayBuilder, SetBuilder setBuilder);
}
