// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.Ratio;
import com.cognitect.transit.impl.TaggedValue;

public class RatioHandler implements Handler {

    @Override
    public String tag(Object o) {
        return "ratio";
    }

    @Override
    public Object rep(Object o) {
        Ratio r = (Ratio)o;
        long[] l = {r.numerator, r.denominator};
        return new TaggedValue("array", l);
    }

    @Override
    public String stringRep(Object o) {
        return null;
    }
}
