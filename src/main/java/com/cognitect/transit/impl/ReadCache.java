package com.cognitect.transit.impl;

import com.cognitect.transit.Writer;

public class ReadCache {

    private String[] cache;
    private int index;

    public ReadCache() {

        cache = new String[WriteCache.MAX_CACHE_ENTRIES];
        index = 0;
    }

    private boolean cacheCode(String s) {

        if(s.charAt(0) == Writer.SUB)
            return true;
        else return false;
    }

    private int codeToIndex(String s) {

        return (int)s.charAt(1) - WriteCache.BASE_CHAR_IDX;
    }

    public String cacheRead(String s, boolean asMapKey) {

        if(s.length() != 0) {
            if(WriteCache.isCacheable(s, asMapKey)) {
                if(index == WriteCache.MAX_CACHE_ENTRIES) {
                    index = 0;
                }
                cache[index++] = s;
            }
            else if(cacheCode(s)) {
                return cache[codeToIndex(s)];
            }
        }
        return s;
    }
}
