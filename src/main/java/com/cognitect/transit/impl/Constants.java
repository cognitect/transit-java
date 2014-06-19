package com.cognitect.transit.impl;

public final class Constants {
    public static final char ESC = '~';
    public static final String ESC_STR = String.valueOf(ESC);
    public static final char TAG = '#';
    public static final String TAG_STR = String.valueOf(TAG);
    public static final char SUB = '^';
    public static final String SUB_STR = String.valueOf(SUB);
    public static final char RESERVED = '`';
    public static final String ESC_TAG = String.valueOf(ESC) + TAG;
    public static final String QUOTE_TAG = ESC_TAG + "'";
    public static final String MAP_AS_ARRAY = "^ ";
}
