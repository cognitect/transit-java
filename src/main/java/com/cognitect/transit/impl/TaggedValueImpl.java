// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.TaggedValue;

public class TaggedValueImpl implements TaggedValue {

    private final String tag;
    private final Object rep;

    public TaggedValueImpl(String tag, Object rep) {
        this.tag = tag;
        this.rep = rep;
    }

    public String getTag() {
        return tag;
    }

    public Object getRep() {
        return rep;
    }

    @Override
    public boolean equals(Object o) {

        if(this == o)
            return true;

        if(!(o instanceof TaggedValueImpl))
            return false;

        TaggedValueImpl other = (TaggedValueImpl)o;
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
