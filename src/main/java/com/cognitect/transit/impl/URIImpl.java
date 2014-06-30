// All rights reserved.
// Copyright (c) Cognitect, Inc.

package com.cognitect.transit.impl;

import com.cognitect.transit.URI;

public class URIImpl implements URI, Comparable<URI> {

    String uri;

    public URIImpl(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return uri;
    }

    @Override
    public String getValue() { return uri; }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(o instanceof URIImpl && ((URIImpl)o).getValue().equals(getValue()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {
        return 19 * getValue().hashCode();
    }

    @Override
    public int compareTo(URI uri) {
        return getValue().compareTo(((URIImpl)uri).getValue());
    }
}
