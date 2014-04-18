// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.TaggedValue;

public class TaggedValueHandler implements Handler {

    @Override
    public String tag(Object o) {
        return ((TaggedValue)o).getTag();
    }

    @Override
    public Object rep(Object o) {
        return ((TaggedValue)o).getRep();
    }

    @Override
    public String stringRep(Object ignored) {
        return null;
    }
}
