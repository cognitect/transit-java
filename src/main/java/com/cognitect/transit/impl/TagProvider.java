// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

public interface TagProvider {
    public String getTag(Object o);
    public String getTagAfterPossibleTransform(Object o);
}
