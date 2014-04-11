// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class Keyword implements Comparable<Keyword> {

    private final String value;

    public Keyword(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        if(o == this)
            return true;

        if(o instanceof Keyword && ((Keyword)o).value.equals(value))
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

        return value.compareTo(keyword.value);
    }
}
