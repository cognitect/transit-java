// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

public class ReadCache {

    private String[] cache;
    private int index;

    public ReadCache() {

        cache = new String[WriteCache.MAX_CACHE_ENTRIES];
        index = 0;
    }

    private boolean cacheCode(String s) {

        if((s.charAt(0) == Constants.SUB) && (!s.equals(Constants.MAP_AS_ARRAY)))
            return true;
        else
            return false;
    }

    private int codeToIndex(String s) {

        int sz = s.length();
        if (sz == 2) {
            return ((int)s.charAt(1) - WriteCache.BASE_CHAR_IDX);
        } else {
            return (((int)s.charAt(1) - WriteCache.BASE_CHAR_IDX) * 94) +
                    ((int)s.charAt(2) - WriteCache.BASE_CHAR_IDX);
        }
    }

    public String cacheRead(String s, boolean asMapKey) {

        if(s.length() != 0) {
            if(cacheCode(s)) {
                return cache[codeToIndex(s)];
            } else if(WriteCache.isCacheable(s, asMapKey)) {
                if(index == WriteCache.MAX_CACHE_ENTRIES) {
                    init();
                }
                cache[index++] = s;
            }
        }
        return s;
    }

	public ReadCache init(){
		//need not clear array
		index = 0;
		return this;
	}
}
