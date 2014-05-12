// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Keyword;

public class KeywordImpl implements Comparable<Keyword>, Keyword {

    private final String value;

    public KeywordImpl(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {

        if(o == this)
            return true;

        if(o instanceof KeywordImpl && ((KeywordImpl)o).value.equals(value))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {

        return 17 * value.hashCode();
    }

    @Override
    public int compareTo(Keyword keyword) {

        return value.compareTo(((KeywordImpl)keyword).value);
    }
}
