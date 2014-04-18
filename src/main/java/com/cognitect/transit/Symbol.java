// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class Symbol implements Comparable<Symbol> {

    private final String value;

    public Symbol(String s) {
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

        if(o instanceof Symbol && ((Symbol)o).value.equals(value))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {

        return 19 * value.hashCode();
    }

    @Override
    public int compareTo(Symbol symbol) {

        return value.compareTo(symbol.value);
    }
}
