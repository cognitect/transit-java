// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

public class AsTag {

    public final String tag;
    public final Object rep;
    public final String repString;

    public AsTag(String tag, Object rep, String repString) {
        this.tag = tag;
        this.rep = rep;
        this.repString = repString;
    }
}
