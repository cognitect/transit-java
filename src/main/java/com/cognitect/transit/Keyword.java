package com.cognitect.transit;

public class Keyword {

    public final String value;

    public Keyword(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Keyword && ((Keyword)o).value == value)
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
