// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Keyword;

public class KeywordImpl implements Comparable<Keyword>, Keyword {

    final String ns;
    final String name;
    transient String _str;
    transient int _hash;

    public KeywordImpl(String nsname) {
        int i = nsname.indexOf('/');
        if(i == -1) {
            ns = null;
            name = nsname.intern();
        } else {
            ns = nsname.substring(0, i).intern();
            name = nsname.substring(i + 1).intern();
        }
    }

    @Override
    public String toString() {
        if(_str == null){
            if(ns != null)
                _str = ":" + ns + "/" + name;
            else
                _str = ":" + name;
        }
        return _str;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNamespace() {
        return ns;
    }

    @Override
    public boolean equals(Object o) {

        if(o == this)
            return true;

        //N.B. relies on interned strings
        return o instanceof Keyword &&
           ((Keyword)o).getNamespace() == ns &&
           ((Keyword)o).getName() == name;

    }

    @Override
    public int hashCode() {
        if(_hash == 0)
            _hash = 17 * toString().hashCode();
        return _hash;
    }

    @Override
    public int compareTo(Keyword keyword) { return toString().compareTo(keyword.toString()); }
}
