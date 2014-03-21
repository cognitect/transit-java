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
}
