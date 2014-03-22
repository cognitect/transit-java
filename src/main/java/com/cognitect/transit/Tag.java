package com.cognitect.transit;

public enum Tag {

    NULL ("_", true),
    STRING ("s", true),
    BOOLEAN ("?", true);

    private final String tag;
    public final boolean allowedMapKey;
    Tag(String tag, Boolean allowedMapKey) {
        this.tag = tag;
        this.allowedMapKey = allowedMapKey;
    }
    public String toString() {
        return tag;
    }
}
