// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.AbstractParser;

import java.util.Date;

public class TimeHandler implements Handler {

    @Override
    public String tag(Object ignored) {
        return "t";
    }

    @Override
    public Object rep(Object o) {
        return AbstractParser.dateTimeFormat.format((Date)o);
    }

    @Override
    public String stringRep(Object o) {
        return (String)rep(o);
    }
}
