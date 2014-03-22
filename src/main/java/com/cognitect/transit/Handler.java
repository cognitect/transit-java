package com.cognitect.transit;

public interface Handler {

    Tag tag(Object o);
    Object rep(Object o);
    String stringRep(Object o);
}
