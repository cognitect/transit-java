package com.cognitect.transit;

public enum Tag {

    NULL        ("_", true),
    STRING      ("s", true),
    BOOLEAN     ("?", true),
    INTEGER     ("i", true),
    DOUBLE      ("d", true),
    BIG_DECIMAL ("f", true),
    CHARACTER   ("c", true),
    KEYWORD     (":", true),
    SYMBOL      ("$", true),
    BINARY      ("b", true),
    UUID        ("u", true),
    URI         ("r", true),
    MAP         ("map", false);

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
