// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.TaggedValue;

public class TaggedValueImpl<T> implements TaggedValue<T> {

    private final String tag;
    private final T rep;

    public TaggedValueImpl(String tag, T rep) {
        this.tag = tag;
        this.rep = rep;
    }

    public String getTag() {
        return tag;
    }

    public T getRep() {
        return rep;
    }

    @Override
    public boolean equals(Object o) {

        if(this == o)
            return true;

        if(!(o instanceof TaggedValueImpl))
            return false;

        TaggedValueImpl<T> other = (TaggedValueImpl<T>)o;
        return (this.tag.equals(other.getTag()) &&
                this.rep.equals(other.getRep()));
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result * tag.hashCode();
        result = 31 * result * rep.hashCode();

        return result;
    }
}
