// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

public class ReadCache {

    private Object[] cache;
    private int index;

    public ReadCache() {
        cache = new Object[WriteCache.MAX_CACHE_ENTRIES];
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
            return (((int)s.charAt(1) - WriteCache.BASE_CHAR_IDX) * WriteCache.CACHE_CODE_DIGITS) +
                    ((int)s.charAt(2) - WriteCache.BASE_CHAR_IDX);
        }
    }

    public Object cacheRead(String s, boolean asMapKey) { return cacheRead(s, asMapKey, null); }

    public Object cacheRead(String s, boolean asMapKey, AbstractParser p) {

        if(s.length() != 0) {
            if(cacheCode(s)) {
                return cache[codeToIndex(s)];
            } else if(WriteCache.isCacheable(s, asMapKey)) {
                if(index == WriteCache.MAX_CACHE_ENTRIES) {
                    init();
                }
                return cache[index++] = (p != null ? p.parseString(s) : s);
            }
        }
        return p != null ? p.parseString(s) : s;
    }

	public ReadCache init(){
		//need not clear array
		index = 0;
		return this;
	}
}
