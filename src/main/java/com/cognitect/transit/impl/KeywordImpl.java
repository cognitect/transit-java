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
    public String name() {
        return name;
    }

    @Override
    public String namespace() {
        return ns;
    }

    @Override
    public String value() { return toString(); }

    @Override
    public boolean equals(Object o) {

        if(o == this)
            return true;

        if(o instanceof KeywordImpl && ((KeywordImpl)o).value().equals(value()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() { return 17 * value().hashCode(); }

    @Override
    public int compareTo(Keyword keyword) { return value().compareTo(((KeywordImpl)keyword).value()); }
}
