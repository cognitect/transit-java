package com.cognitect.transit;

public class Symbol {

    public final String value;

    public Symbol(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Symbol && ((Symbol)o).value == value)
            return true;
        else
            return false;
    }
}
