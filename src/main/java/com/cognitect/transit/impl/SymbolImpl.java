// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Symbol;

public class SymbolImpl implements Symbol, Comparable<Symbol> {

    private final String value;

    public SymbolImpl(String s) {
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

        if(o instanceof SymbolImpl && ((SymbolImpl)o).value.equals(value))
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

        return value.compareTo(((SymbolImpl)symbol).value);
    }
}
