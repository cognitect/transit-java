// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Keyword;
import com.cognitect.transit.Named;

public class KeywordImpl implements Comparable<Keyword>, Keyword, Named {

    final String ns;
    final String name;
    String _str;

    public KeywordImpl(String nsname) {
        int i = nsname.indexOf('/');
        if(i == -1 || nsname.equals("/")) {
            ns = null;
            name = nsname.intern();
        } else {
            ns = nsname.substring(0, i);
            name = nsname.substring(i + 1);
        }
    }

    @Override
    public String toString() {
        if(_str == null){
            if(ns != null)
                _str = (ns + "/" + name).intern();
            else
                _str = name;
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
    public String getValue() { return toString(); }

    @Override
    public boolean equals(Object o) {

        if(o == this)
            return true;

        if(o instanceof KeywordImpl && ((KeywordImpl)o).getValue().equals(getValue()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() { return 17 * getValue().hashCode(); }

    @Override
    public int compareTo(Keyword keyword) { return getValue().compareTo(((KeywordImpl)keyword).getValue()); }
}
