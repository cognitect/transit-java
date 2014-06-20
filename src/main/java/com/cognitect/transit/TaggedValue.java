// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public interface TaggedValue {
    public String tag();
    public Object rep();
    public String stringRep();
}
