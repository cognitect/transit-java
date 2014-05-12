// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.TaggedValue;

public class TaggedValueImpl implements TaggedValue {

    private final String tag;
    private final Object rep;
    private final String repStr;

    public TaggedValueImpl(String tag, Object rep) {
        this.tag = tag;
        this.rep = rep;
        this.repStr = null;
    }

    public TaggedValueImpl(String tag, Object rep, String repStr) {
        this.tag = tag;
        this.rep = rep;
        this.repStr = repStr;
    }

    public String getTag() {
        return tag;
    }

    public Object getRep() {
        return rep;
    }

    public String getRepStr() { return repStr; }

    @Override
    public boolean equals(Object o) {

        if(this == o)
            return true;

        if(!(o instanceof TaggedValueImpl))
            return false;

        TaggedValueImpl other = (TaggedValueImpl)o;
        if(this.tag.equals(other.getTag()) && this.rep.equals(other.getRep()))
            return true;
        else
            return false;
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result * tag.hashCode();
        result = 31 * result * rep.hashCode();

        return result;
    }
}
