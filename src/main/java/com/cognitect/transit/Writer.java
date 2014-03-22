package com.cognitect.transit;

public interface Writer {

    public static final char ESC = '~';
    public static final char TAG = '#';
    public static final char SUB = '^';
    public static final char RESERVED = '`';
    public static final String ESC_TAG = "~#";
    public static final int MIN_SIZE_CACHEABLE = 3;
    public static final int MAX_CACHE_ENTRIES = 94;
    public static final int BASE_CHAR_IDX = 33;

    void write(Object o) throws Exception;
}
